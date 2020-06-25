package jp.geisha.yourgallery

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediasViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
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

    val photosData :MutableLiveData<List<Photo>> = MutableLiveData()

    fun getPhotos(){
        viewModelScope.launch(Dispatchers.Default) {
            val data = fetch()
            photosData.postValue(data)
        }
    }

    private suspend fun fetch(): List<Photo> = withContext(Dispatchers.IO) {
        val photosList = mutableListOf<Photo>()
        getApplication<Application>().contentResolver.query(
            PHOTO_URI,
            PHOTO_PROJECTION,
            null,
            null,
            PHOTO_SORT_ORDER
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(PHOTO_ID)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(PHOTO_DATE_ADDED)
            val fileNameIndex = cursor.getColumnIndexOrThrow(PHOTO_FILE_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val dateAdded = cursor.getLong(dateAddedIndex)
                val fileName = cursor.getString(fileNameIndex) ?: continue
                val uri = ContentUris.withAppendedId(PHOTO_URI, id)
                photosList.add(
                    Photo(fileName, dateAdded, uri)
                )
            }
        }
        photosList
    }
}