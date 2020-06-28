package jp.geisha.yourgallery.label

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LabelsAdapter : RecyclerView.Adapter<LabelsViewHolder>() {

    private val labelItems: MutableMap<String, Uri?> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
        return LabelsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return labelItems.size
    }

    override fun onBindViewHolder(holder: LabelsViewHolder, position: Int) {
        holder.bind(labelItems.toList()[position])
    }

    fun updateLabels(labels :Map<String, Uri?>){
        labelItems.clear()
        labelItems.putAll(labels)
        notifyDataSetChanged()
    }
}