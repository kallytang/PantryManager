package dev.kallytang.chompalpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.google.firebase.firestore.ktx.firestore
import dev.kallytang.chompalpha.adapters.ItemsAdapter
import dev.kallytang.chompalpha.adapters.StorageLocationAdapter
import dev.kallytang.chompalpha.databinding.ActivityMainBinding
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Unit
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.dialog_text_input.*

class MainActivity : AppCompatActivity() , FilterItems{
    private companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var itemsList: MutableList<Item>
    private lateinit var itemsListCopy: MutableList<Item>
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var storageList:MutableList<String>
    private lateinit var storageAdapter: StorageLocationAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)
        auth = Firebase.auth

        if((applicationContext as MyApplication).storageLocationList.isNullOrEmpty()){
            (applicationContext as MyApplication).queryStorageLocations()
        }

//        binding.swipeRefreshMain.setOnRefreshListener {
//            getItems()
//        }
//        getItems()
        //instantiate list
        itemsList = mutableListOf()
        itemsListCopy = mutableListOf()

        val decoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        //create adapter
        itemsAdapter = ItemsAdapter(this, itemsList as ArrayList<Item>)
        binding.rvPantryItems.adapter = itemsAdapter
        binding.rvPantryItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPantryItems.addItemDecoration(decoration)

        // for the storage list tabs
        storageList= mutableListOf()
        storageAdapter = StorageLocationAdapter(this, storageList, this)
        
        binding.rvListTabs.adapter = storageAdapter
        binding.rvListTabs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getUnits()
        getStorageLocations()

        // removing shadow of action bar
        supportActionBar?.elevation = 0F;

        //for logging out

//        val fab: View = findViewById(R.id.fab_add_pantry_item)
        binding.fabAddPantryItem.setOnClickListener {
            val intent = Intent(this, AddFoodItemActivity::class.java)
            startActivity(intent)
        }



    }

//    override fun onResume() {
//        super.onResume()
//        itemsAdapter.notifyDataSetChanged()
//    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_tab->{
                Log.i(TAG, "Logged out")
                auth.signOut()
                val logoutIntent = Intent(this, LoginActivity::class.java)
                logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(logoutIntent)

            }
            R.id.add_storage_location ->{
                var dialogue = AddStorageLocationDialogue()
                dialogue.show(supportFragmentManager, "Add storageLocation")
            }
        }

        return  super.onOptionsItemSelected(item)
    }
    private fun getStorageLocations() {
        if((applicationContext as MyApplication).storageLocationList.isNullOrEmpty()){
            // query from database
            db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { snapshot->
                var user = snapshot.toObject(User::class.java)
                if (user!=null){
                    user.myPantry?.get()?.addOnSuccessListener { pantryDoc ->
                        val location:Map<String, String> = pantryDoc.get("storage_locations") as Map<String, String>
                        val listLocation = ArrayList(location.values)
                        listLocation.sort()
                        listLocation.add(0, "All")

                        storageList.clear()
                        storageList.addAll(listLocation)
                        storageAdapter.notifyDataSetChanged()
                    }
                }

            }
        }else{
            storageList.clear()
            (applicationContext as MyApplication).storageLocationList?.let { storageList.addAll(it) }
            storageList.add(0,"All")
            storageAdapter.notifyDataSetChanged()
        }
    }



    private fun getUnits(){
//        var myApp = MyApplication()
        db.collection("units").get().addOnSuccessListener { snapshot ->
            Log.i("units", snapshot.toObjects(Unit::class.java).toString())
            (applicationContext as MyApplication).unitList = snapshot.toObjects(Unit::class.java)

            Log.i("unitsList2", (applicationContext as MyApplication).unitList.toString())
        }
    }

    private fun getItems() {
        if ((applicationContext as MyApplication).pantryRef != null){
            (applicationContext as MyApplication).pantryRef?.collection("my_pantry")
                ?.get()?.addOnSuccessListener { snap ->
                val items:MutableList<Item> = snap.toObjects(Item::class.java)
                Log.i("itemsF", items.toString())
                itemsAdapter.clear()
                itemsAdapter.addAll(items as ArrayList<Item>)
                itemsListCopy.clear()
                itemsListCopy.addAll(items)
                binding.swipeRefreshMain.isRefreshing = false

            }
        }else{
            db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener{ doc ->
                val user = doc.toObject(User::class.java)
                Log.i("itemsF", user.toString())
                if (user != null) {
                    user.myPantry?.collection("my_pantry")?.get()?.addOnSuccessListener { snap ->
                        val items:MutableList<Item> = snap.toObjects(Item::class.java)
                        Log.i("itemsF", items.toString())
                        itemsAdapter.clear()
                        itemsAdapter.addAll(items as ArrayList<Item>)
                        itemsListCopy.clear()
                        itemsListCopy.addAll(items)
                        binding.swipeRefreshMain.isRefreshing = false

                    }
                }

            }
        }



    }

    override fun filterItems(locationName: String){
        Log.i("itemsClicked", locationName)
        if(locationName == "All"){
            itemsAdapter.clear()
            itemsAdapter.addAll(itemsListCopy as ArrayList<Item>)
        }else{
            var listCopy = mutableListOf<Item>()
            itemsList.clear()
            itemsList.addAll(itemsListCopy)
            for(items in itemsList){
                if(items.location == locationName){
                    listCopy.add(items)
                }
            }
            itemsAdapter.clear()
            itemsAdapter.addAll(listCopy as ArrayList<Item>)
        }
    }
}



