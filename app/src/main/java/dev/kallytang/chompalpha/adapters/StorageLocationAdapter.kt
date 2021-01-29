package dev.kallytang.chompalpha.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kallytang.chompalpha.R
import kotlinx.android.synthetic.main.item_storage_name.view.*


class StorageLocationAdapter(val context: Context, val locationList:List<String>) :
    RecyclerView.Adapter<StorageLocationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bindView(locationItem: String) {
            itemView.tv_location_item.text = locationItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_storage_name, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(locationList[position])
    }

    override fun getItemCount(): Int {
        return locationList.size
    }
}