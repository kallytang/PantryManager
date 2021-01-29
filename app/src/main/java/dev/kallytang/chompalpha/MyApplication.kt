package dev.kallytang.chompalpha

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import java.util.ArrayList

class MyApplication : Application() {
    private lateinit var auth: FirebaseAuth

    var unitsList: List<Units>? = null


    var storageLocationList: MutableList<String>? = null

    var unitsAsString: MutableList<String>? = null
    var currUser: User? = null
    var pantryRef: DocumentReference? = null


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        auth = Firebase.auth


        db.collection("units").get().addOnSuccessListener { snapshot ->
            val dataList = snapshot.toObjects(Units::class.java)
            unitsAsString = mutableListOf<String>()
            unitsList = dataList
            Log.i("units", unitsList.toString())
            for (items in unitsList!!) {
                unitsAsString?.add(items.abbreviation)
            }
            Log.i("units", unitsAsString.toString())


       }
        db.collection("users").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { doc ->
                currUser = doc.toObject(User::class.java)
                // get reference to pantry
                val pantryReference: DocumentReference? =  currUser?.myPantry
                pantryReference?.get()
                    ?.addOnSuccessListener { pantryDoc ->
                        val location:Map<String, String> = pantryDoc.get("storage_locations") as Map<String, String>
                        val listLocation = ArrayList(location.values)
                        listLocation.sort()
                        val locationStrings = listLocation.toMutableList()
                        // set data to lists in application context and on main activity
                        storageLocationList = locationStrings
                        pantryRef =pantryReference
                        Log.i("chips2", pantryRef.toString())


                    }
            }
    }


    }
    fun queryStorageLocation(){


}