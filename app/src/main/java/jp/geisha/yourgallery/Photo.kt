package jp.geisha.yourgallery

import android.net.Uri

data class Photo(
    override val detail: Detail
) : Media {
    companion object {
        fun newInstance(fileName: String, dateAdded: Long, uri: Uri) =
            Photo(Detail(fileName, dateAdded, uri))
    }
}
