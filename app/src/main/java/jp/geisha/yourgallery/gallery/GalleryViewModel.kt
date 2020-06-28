package jp.geisha.yourgallery.gallery

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import jp.geisha.yourgallery.label.OnDeviceImageLabeler
import jp.geisha.yourgallery.data.GalleryPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    sealed class State {
        data class Loading(val progress: Float) : State()
        object Success : State()
    }

    companion object {
        const val PAGE_SIZE = 20
        private val PHOTO_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private const val PHOTO_ID = MediaStore.Images.ImageColumns._ID
        private const val PHOTO_DATE_ADDED = MediaStore.Images.ImageColumns.DATE_ADDED
        private val PHOTO_PROJECTION = arrayOf(
            PHOTO_ID
        )
        private const val PHOTO_SORT_ORDER = "$PHOTO_DATE_ADDED DESC, $PHOTO_ID ASC"
    }

    val labelsData = MutableLiveData<Map<String, Uri?>>()
    val labelScanState = MutableLiveData<State>()

    init {
        getLabels()
    }

    val galleryDataFlow = Pager(
        PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE)
    ) {
        GalleryPagingSource(getApplication())
    }.flow.cachedIn(viewModelScope)

    private fun getLabels() {
        labelScanState.postValue(State.Loading(0f))
        viewModelScope.launch(Dispatchers.Default) {
            val labelsByUri = fetchLabelsByUri()
            val labelItems = labelsByUri
                .values
                .flatten()
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .map { it.key }
                .associateWith { label ->
                    labelsByUri
                        .toList()
                        .find {
                            it.second.contains(label)
                        }
                        ?.first
                }
            labelsData.postValue(labelItems)
            delay(1000)
            labelScanState.postValue(State.Success)
        }
    }

    private suspend fun fetchLabelsByUri() = withContext(Dispatchers.IO) {
        val labelsByUri = mutableMapOf<Uri, List<String>>()
        getApplication<Application>().contentResolver.query(
            PHOTO_URI,
            PHOTO_PROJECTION,
            null,
            null,
            PHOTO_SORT_ORDER
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(PHOTO_ID)
            var count = 0
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val uri = ContentUris.withAppendedId(PHOTO_URI, id)
                val labels =
                    OnDeviceImageLabeler.detectLabel(
                        getApplication(),
                        uri
                    )
                labelsByUri[uri] = labels
                count++
                val progressPercent = (count.toFloat() / cursor.count.toFloat()) * 100
                labelScanState.postValue(State.Loading(progressPercent))
            }
            cursor.close()
        }
        labelsByUri
    }
}