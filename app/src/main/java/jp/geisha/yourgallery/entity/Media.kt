package jp.geisha.yourgallery.entity

import android.net.Uri

data class Detail(
    val name: String,
    val date: Long,
    val uri: Uri,
    var label: String? = null
)

interface Media {
    val detail: Detail
}