package jp.geisha.yourgallery.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.geisha.yourgallery.R
import jp.geisha.yourgallery.R.layout.list_item_photo
import jp.geisha.yourgallery.entity.Photo
import kotlinx.android.synthetic.main.list_item_photo.view.image

class PhotoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(list_item_photo, parent, false)
) {
    fun bind(photo: Photo) {
        Glide.with(itemView)
            .load(photo.detail.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
            )
            .into(itemView.image)
    }
}