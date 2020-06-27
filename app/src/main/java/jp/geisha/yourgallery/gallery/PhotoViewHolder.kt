package jp.geisha.yourgallery.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import jp.geisha.yourgallery.R
import jp.geisha.yourgallery.R.layout.list_item_photo
import jp.geisha.yourgallery.entity.Photo
import kotlinx.android.synthetic.main.list_item_photo.view.*

class PhotoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(list_item_photo, parent, false)
) {
    fun bind(photo: Photo) {
        itemView.image.apply {
            load(photo.detail.uri) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                memoryCachePolicy(CachePolicy.ENABLED)
            }
        }
    }
}