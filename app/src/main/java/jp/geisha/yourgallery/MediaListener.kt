package jp.geisha.yourgallery

object MediaListener {

    interface OnMediaSelectedListener {
        fun onSelected(data: Photo, position: Int)
        fun onLongClick(data: Photo, position: Int)
    }
}