package dev.kallytang.chompalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.activity_add_food_item.*

class AddFoodItemActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var unitsList = mutableListOf<Units>()
    private var unitsStrings = mutableListOf<String>()
    private var storageLocationList = mutableListOf<String>()
    private lateinit var spinner: Spinner
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_item)
        auth = Firebase.auth
        getUnitData()

        var spinner:Spinner = findViewById(R.id.add_unit_dropdown)
        var calendarBtn:ImageView = findViewById(R.id.iv_calendar_btn)
        var foodName: EditText = findViewById(R.id.et_item_name)
        var brandName: EditText = findViewById(R.id.et_brand)
        var notes: EditText = findViewById(R.id.et_food_notes)

        calendarBtn.setOnClickListener {
            // TODO set up calendar picker dialog
        }


    }

    private fun getUnitData(){
        unitsList = (applicationContext as MyApplication).unitsList!!
        unitsStrings = (applicationContext as MyApplication).unitsAsString!!

        // check if pantry list already initialized, if not, set the data
        if ((applicationContext as MyApplication).storageLocationList == null){
            db.collection("users").document(auth.currentUser?.uid.toString()).get()
                .addOnSuccessListener { doc ->
                (applicationContext as MyApplication).currUser = doc.toObject(User::class.java)

                (applicationContext as MyApplication).currUser?.myPantry?.get()
                    ?.addOnSuccessListener { pantryDoc ->
                        var location:Map<String, String> = pantryDoc.get("storage_locations") as Map<String, String>
                        val listLocation = ArrayList(location.keys)
                        listLocation.sort()
                        var locationStrings = mutableListOf<String>()
                        locationStrings = listLocation.toMutableList()
                        (applicationContext as MyApplication).storageLocationList= locationStrings
                        storageLocationList = locationStrings

                        Log.i("location", (applicationContext as MyApplication).storageLocationList.toString())

                    }


            }
            // if the list exists in the application context
        }else{
            storageLocationList = (applicationContext as MyApplication).storageLocationList!!

        }

    }

//    https://material.io/components/chips#types
}