package jp.geisha.yourgallery.gallery

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.geisha.yourgallery.entity.Media
import jp.geisha.yourgallery.entity.Photo
import java.lang.IllegalStateException

class GalleryAdapter : PagingDataAdapter<Media,RecyclerView.ViewHolder>(diffCallback){

    enum class Type {
        PHOTO
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Type.PHOTO.ordinal -> PhotoViewHolder(
                parent
            )
            else -> throw IllegalStateException("It's a Unknown viewHolder.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val media = getItem(position)
        when (holder){
            is PhotoViewHolder -> holder.bind(media as Photo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is Photo -> Type.PHOTO.ordinal
            else -> throw IllegalStateException("It's a Illegal view type.")
        }
    }
}
