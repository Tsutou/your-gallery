package jp.geisha.yourgallery

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MediasViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val PAGE_SIZE = 20

        private val PHOTO_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private const val PHOTO_ID = MediaStore.Images.ImageColumns._ID
        private const val PHOTO_DATE_ADDED = MediaStore.Images.ImageColumns.DATE_ADDED
        private const val PHOTO_FILE_NAME = MediaStore.Images.ImageColumns.DISPLAY_NAME
        private val PHOTO_PROJECTION = arrayOf(
            PHOTO_ID,
            PHOTO_DATE_ADDED,
            PHOTO_FILE_NAME
        )
        private const val PHOTO_SORT_ORDER = "$PHOTO_DATE_ADDED DESC, $PHOTO_ID ASC"
    }

    val photosDataFlow = Pager(
        PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE)
    ) {
        MediaPagingSource(getApplication())
    }.flow
}