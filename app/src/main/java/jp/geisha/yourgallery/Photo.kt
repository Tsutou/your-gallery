package jp.geisha.yourgallery

import android.net.Uri

data class Photo(
    val fileName: String,
    val dateAdded: Long,
    val uri: Uri
) : Media
