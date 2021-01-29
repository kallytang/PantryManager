package dev.kallytang.chompalpha.adapters

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kallytang.chompalpha.R
import dev.kallytang.chompalpha.models.Item
import kotlinx.android.synthetic.main.item_food_list.view.*

class ItemsAdapter(val context: Context, val items: List<Item> ) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bindView(item: Item) {
            itemView.tv_title_item.text =  item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}