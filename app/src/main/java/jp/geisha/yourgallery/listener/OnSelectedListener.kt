package jp.geisha.yourgallery.listener

import jp.geisha.yourgallery.entity.Media

interface OnSelectedListener {
    fun onClick(data: Media, position: Int)
    fun onLongClick(data: Media, position: Int)
}