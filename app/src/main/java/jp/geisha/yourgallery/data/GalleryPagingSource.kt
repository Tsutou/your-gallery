package jp.geisha.yourgallery.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.paging.PagingSource
import jp.geisha.yourgallery.entity.Media
import jp.geisha.yourgallery.entity.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryPagingSource(private val context: Context) : PagingSource<Long, Media>() {

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
        private const val PHOTO_SORT_ORDER = "$PHOTO_DATE_ADDED DESC, $PHOTO_ID ASC LIMIT 20"
        private const val PHOTO_FROM_DATE_ADDED = "$PHOTO_DATE_ADDED <=?"
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Media> {
        return try {
            val pagedKeyDate = params.key ?: System.currentTimeMillis()
            val result = fetch(pagedKeyDate)
            val lastItemDate = result.last().detail.date
            if (pagedKeyDate == lastItemDate) {
                return LoadResult.Error(IllegalAccessException("There doesn't seem to be a next page."))
            }
            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = lastItemDate
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetch(pageKey: Long): List<Media> = withContext(Dispatchers.IO) {
        val photosList = mutableListOf<Media>()
        context.contentResolver.query(
            PHOTO_URI,
            PHOTO_PROJECTION,
            PHOTO_FROM_DATE_ADDED,
            arrayOf(pageKey.toString()),
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
                    Photo.newInstance(
                        fileName,
                        dateAdded,
                        uri
                    )
                )
            }
            cursor.close()
        }
        photosList
    }
}