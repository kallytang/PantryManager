package dev.kallytang.chompalpha

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User

class MyApplication : Application() {
    private lateinit var auth: FirebaseAuth

    var unitsList: MutableList<Units>? = null
    var storageLocationList: MutableList<String>? = null
    var unitsAsString: MutableList<String>? = null
    var currUser: User? = null


    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("units").get().addOnSuccessListener { snapshot ->
            var dataList = snapshot.toObjects(Units::class.java)
            unitsAsString = mutableListOf<String>()
            unitsList = dataList
            Log.i("units", unitsList.toString())
            for (items in unitsList!!) {
                unitsAsString?.add(items.abbreviation)
            }
            Log.i("units", unitsAsString.toString())


       }
    }

}