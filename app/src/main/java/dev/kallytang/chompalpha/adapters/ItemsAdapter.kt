package dev.kallytang.chompalpha.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.EditItemActivity
import dev.kallytang.chompalpha.R
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Unit
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.item_food_list.view.*

class ItemsAdapter(val context: Context, val items: ArrayList<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bindView(item: Item) {
            auth = Firebase.auth
            itemView.tv_title_item.text =  item.name
            itemView.setOnClickListener{
                val intent = Intent(context, EditItemActivity::class.java)
                intent.putExtra("item", item)
                var unit: Unit? = item.units
                context.startActivity(intent)

            }
            itemView.iv_more_info.setOnClickListener{
               if( itemView.item_delete_btn.visibility != View.VISIBLE){
                   itemView.item_delete_btn.visibility = View.VISIBLE
               }else{
                   itemView.item_delete_btn.visibility = View.GONE
               }
            }
            itemView.item_delete_btn.setOnClickListener{
                deleteFromDatabase(item)
                removeAt(layoutPosition )

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
    fun removeAt(position: Int){
        items.removeAt(position);
        notifyItemRemoved(position)
        notifyDataSetChanged()

    }
    fun deleteFromDatabase(item: Item){

        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { snapshot ->
            var user = snapshot.toObject(User::class.java)
            var pantryRef = user?.myPantry
            pantryRef?.collection("my_pantry")?.document(item.documentId.toString())
                ?.delete()?.addOnSuccessListener { Log.d("deleteItem", "DocumentSnapshot successfully deleted!") }
                ?.addOnFailureListener { e -> Log.w("deleteItem", "Error deleting document", e) }

        }


    }

}
