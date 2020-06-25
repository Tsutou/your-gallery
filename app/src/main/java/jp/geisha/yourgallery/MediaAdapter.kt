package jp.geisha.yourgallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MediaAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val photosList = mutableListOf<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return photosList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photo = photosList[position]
        when (holder){
            is PhotoViewHolder -> holder.bind(photo)
        }
    }

    fun submitList(data: List<Photo>){
        photosList.clear()
        photosList.addAll(data)
        notifyDataSetChanged()
    }
}
