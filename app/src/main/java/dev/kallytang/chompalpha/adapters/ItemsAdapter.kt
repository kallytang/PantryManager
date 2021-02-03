package dev.kallytang.chompalpha.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import dev.kallytang.chompalpha.EditItemActivity
import dev.kallytang.chompalpha.R
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Unit
import kotlinx.android.synthetic.main.item_food_list.view.*

class ItemsAdapter(val context: Context, val items: ArrayList<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bindView(item: Item) {
            itemView.tv_title_item.text =  item.name
            itemView.setOnClickListener{
                val intent = Intent(context, EditItemActivity::class.java)
                intent.putExtra("item", item)
                var unit: Unit? = item.units
                context.startActivity(intent)
            }
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
    fun clear(){
        items.clear()

    }
    fun addAll(list: ArrayList<Item>) {
        items.addAll(list)
        notifyDataSetChanged()
    }
}