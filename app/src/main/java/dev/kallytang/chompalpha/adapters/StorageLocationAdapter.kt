package dev.kallytang.chompalpha.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kallytang.chompalpha.FilterItems
import dev.kallytang.chompalpha.R
import kotlinx.android.synthetic.main.item_storage_name.view.*


class StorageLocationAdapter(val context: Context, val locationList:List<String>, private val filter: FilterItems) :
    RecyclerView.Adapter<StorageLocationAdapter.ViewHolder>() {

    private var curr_position = 0
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bindView(locationItem: String) {
            itemView.tv_location_item.text = locationItem
            itemView.setOnClickListener {
                filter.filterItems(locationItem)
                curr_position = layoutPosition
                notifyDataSetChanged()
            }
            if(curr_position == layoutPosition){
                itemView.setBackgroundResource(R.drawable.storage_tab_selected_layout)
            }else{
                itemView.setBackgroundResource(R.drawable.storage_tab_not_selected)
            }
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
