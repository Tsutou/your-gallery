package jp.geisha.yourgallery.gallery

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import jp.geisha.yourgallery.label.OnDeviceImageLabeler
import jp.geisha.yourgallery.data.GalleryPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

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

    init {
        getLabels()
    }

    val galleryDataFlow = Pager(
        PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE)
    ) {
        GalleryPagingSource(getApplication())
    }.flow.cachedIn(viewModelScope)

    private fun getLabels() {
        viewModelScope.launch(Dispatchers.IO) {
            val uriLabelMap = mutableMapOf<Uri, List<String>>()
            val labelsList = mutableListOf<String>()
            getApplication<Application>().contentResolver.query(
                PHOTO_URI,
                PHOTO_PROJECTION,
                null,
                null,
                PHOTO_SORT_ORDER
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(PHOTO_ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val uri = ContentUris.withAppendedId(PHOTO_URI, id)
                    val labels =
                        OnDeviceImageLabeler.detectLabel(
                            getApplication(),
                            uri
                        )
                    uriLabelMap.put(uri, labels)
                    labelsList.addAll(labels)
                }
                cursor.close()
            }
            val imageLabelsMap = labelsList
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .map { it.key }
                .associateWith { label -> uriLabelMap.toList().find { it.second.contains(label) }?.first }
        }
    }
}