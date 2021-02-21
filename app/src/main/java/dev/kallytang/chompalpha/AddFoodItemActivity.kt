package dev.kallytang.chompalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.activity_add_food_item.*
import java.sql.Timestamp

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
        var closeButton: ImageView = findViewById(R.id.iv_exit_add_task)
        var expiryDate: Timestamp

        // set up calendar dialog
        var datePicker =  MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Select an Expiration Date")
        var materialDatePicker  = datePicker.build()

        calendarBtn.setOnClickListener {
            materialDatePicker.show(supportFragmentManager, "DATE_PICKER")

        }
        materialDatePicker.addOnPositiveButtonClickListener { date ->
//            et_date_expiry.setText(materialDatePicker.headerText)

        }

        closeButton.setOnClickListener {

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
                // get reference to pantry
                val pantryRef: DocumentReference? =  (applicationContext as MyApplication).currUser?.myPantry
                    pantryRef?.get()
                    ?.addOnSuccessListener { pantryDoc ->
                        val location:Map<String, String> = pantryDoc.get("storage_locations") as Map<String, String>
                        val listLocation = ArrayList(location.keys)
                        listLocation.sort()
                        val locationStrings = listLocation.toMutableList()
                        // set data to lists in application context and on main activity
                        (applicationContext as MyApplication).storageLocationList= locationStrings
                        storageLocationList = locationStrings
                        (applicationContext as MyApplication).pantryRef = pantryRef

//                        Log.i("location", (applicationContext as MyApplication).storageLocationList.toString())
                    }
            }
            // if the list exists in the application context
        }else{
            storageLocationList = (applicationContext as MyApplication).storageLocationList!!

        }

    }

//    https://material.io/components/chips#types
}