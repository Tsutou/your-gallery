package jp.geisha.yourgallery

object MediaListener {
    interface OnMediaSelectedListener {
        fun onClick(data: Media, position: Int)
        fun onLongClick(data: Media, position: Int)
    }
}