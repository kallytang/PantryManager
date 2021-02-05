package dev.kallytang.chompalpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

class MainActivity : AppCompatActivity() {
    private companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var itemsList: MutableList<Item>
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var storageList:MutableList<String>
    private lateinit var storageAdapter: StorageLocationAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var filterItemsInterface: FilterItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = Firebase.auth



        binding.swipeRefreshMain.setOnRefreshListener {

            getItems()
        }
        getItems()
        //instantiate list
        itemsList = mutableListOf()

        val decoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        //create adapter
        itemsAdapter = ItemsAdapter(this, itemsList as ArrayList<Item>)
        binding.rvPantryItems.adapter = itemsAdapter
        binding.rvPantryItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPantryItems.addItemDecoration(decoration)

        filterItemsInterface = object:FilterItems{
            override fun filterItems(locationName: String){
                when{
                    (locationName == "All") ->{
                        itemsAdapter.clear()
                        itemsAdapter.addAll((applicationContext as MyApplication).itemsList as ArrayList<Item>)
                    }
                    else->{
                        // create a new list
                        var newList =  ArrayList<Item>()
                        //filter items by storage name
                        newList.addAll((applicationContext as MyApplication).itemsList?.filter { item ->
                            item.location == locationName
                        } as ArrayList<Item>)

                    }
                }
            }
        }
        storageList= mutableListOf()
        storageAdapter = StorageLocationAdapter(this, storageList)
        
        binding.rvListTabs.adapter = storageAdapter
        binding.rvListTabs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getUnits()
        getStorageLocations()

        // removing shadow of action bar
        supportActionBar?.elevation = 0F;

        //for logging out

        //todo: make query to firestore to retrieve data

//        val fab: View = findViewById(R.id.fab_add_pantry_item)
        binding.fabAddPantryItem.setOnClickListener {
            val intent = Intent(this, AddFoodItemActivity::class.java)
            startActivity(intent)
        }


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

        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener{ doc ->
            val user = doc.toObject(User::class.java)
            Log.i("itemsF", user.toString())
            if (user != null) {
                user.myPantry?.collection("my_pantry")?.get()?.addOnSuccessListener { snap ->

                    val items:MutableList<Item> = snap.toObjects(Item::class.java)
                    items[1].documentId?.let { Log.i("itemID1", it) }
                    Log.i("itemsF", items.toString())
                    itemsAdapter.clear()
                    itemsAdapter.addAll(items as ArrayList<Item>)
                    binding.swipeRefreshMain.isRefreshing = false
                    (applicationContext as MyApplication).itemsList?.addAll(items)

                }
            }

        }

    }





}