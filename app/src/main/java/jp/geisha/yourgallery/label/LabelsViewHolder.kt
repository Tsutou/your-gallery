package jp.geisha.yourgallery.label

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.geisha.yourgallery.R
import kotlinx.android.synthetic.main.list_item_label.view.*

class LabelsViewHolder (parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.list_item_label, parent, false)
){
    fun bind(label: Pair<String, Uri?>){
        itemView.label.text = label.first
        Glide.with(itemView)
            .load(label.second)
            .into(itemView.image)
    }
}