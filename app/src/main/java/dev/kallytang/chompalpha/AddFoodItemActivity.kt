package dev.kallytang.chompalpha

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.adapters.UnitsSpinnerAdapter
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.list_name_tabs_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddFoodItemActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var storageLocationList = mutableListOf<String>()
    private lateinit var unitSpinner: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var calendarBtn:ImageView
    private lateinit var itemName: EditText
    private lateinit var brandName: EditText
    private lateinit var notes: EditText
    private lateinit var quantityText: EditText
    private lateinit var expiryDate: TextView
    private lateinit var datePicker: MaterialDatePicker.Builder<Long>
    private lateinit var materialDatePicker: MaterialDatePicker<Long>
    private lateinit var closeButton: ImageView
    private lateinit var addLocationChip: Chip
    private lateinit var chipGroup :ChipGroup
    private val stringPatternEditText = "MMM d, yyyy"
    private val timestampPatternFirebase = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private  lateinit var submitButton: Button
    private lateinit var errorNoStorageLocation: TextView
    private lateinit var errorNoExpirationDate: TextView
    private lateinit var unitsList: ArrayList<Units>
    private lateinit var unitsSpinnerAdapter: UnitsSpinnerAdapter
    private lateinit var unitChosen: Units

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_item)
        auth = Firebase.auth

        // get unit data
        findViews()

        // set up calendar dialog
        datePicker =  MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Select an Expiration Date")
        materialDatePicker  = datePicker.build()



//        https://stackoverflow.com/questions/37390080/convert-local-time-to-utc-and-vice-versa

//        var dateFormmatted = DateFormat.format()
//        expiryDate.set


        expiryDate.setOnClickListener{
            expiryDate.isEnabled = false
            materialDatePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        // for when user confirms date, convert date into a timestamp
        materialDatePicker.addOnPositiveButtonClickListener { date ->
            Log.i("date", materialDatePicker.headerText)
            Log.i("date", date.toString())
            expiryDate.setText(materialDatePicker.headerText)
            expiryDate.isEnabled = true
        }
        materialDatePicker.addOnDismissListener {
            expiryDate.isEnabled = true
        }
        materialDatePicker.addOnCancelListener {
            expiryDate.isEnabled = true
        }

        // set up units spinner
        unitsList = ArrayList()

        (applicationContext as MyApplication).unitsList?.let { unitsList.addAll(it) }

        var indexUnit = 0
        for (idx in unitsList.indices){
            if(unitsList[idx].unitName == "none"){
                indexUnit = idx
            }
        }

        unitsSpinnerAdapter = UnitsSpinnerAdapter(this, R.layout.unit_spinner_row, unitsList)
//        Log.i("unitsList",unitsList.toString())
        unitSpinner.adapter = unitsSpinnerAdapter
        unitSpinner.setSelection(indexUnit)

        var unitChoice: Units
        unitSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                unitChosen = parent?.getItemAtPosition(position) as Units
//                var clickedUnitName:String = unitChosen.abbreviation

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        
        //for closing button
        closeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        // inflate chips into chip group
        chipGroup = findViewById(R.id.cg_location_tag_container)
        for (item in (applicationContext as MyApplication).storageLocationList!!){
            Log.i("chips", item)

            val chip = Chip(chipGroup.context)
            chip.text = item
            chipGroup.isSingleSelection = true
            chip.isCheckable = true

            chipGroup.addView(chip)

        }
        var chip = chipGroup.getChildAt(1).id
        var chipSelected: Chip = findViewById(chip)
        chipSelected.isChecked = true

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            errorNoStorageLocation.setText("")
            errorNoStorageLocation.visibility = View.INVISIBLE
//            if (checkedId == R.id.id_add_location_chip){
//                errorNoStorageLocation.setText(R.string.errorStorageSelection)
//                errorNoStorageLocation.setTextColor(Color.RED)
//                errorNoStorageLocation.visibility = View.VISIBLE
//            }
        }
        
        // add location to chip group
        addLocationChip.setOnClickListener {
            // create a dialog to allow user to add new location
            //select new child

        }


        // submit button 
        submitButton.setOnClickListener {
            var itemNameInput = itemName.text.toString()
            var itemBrandUnput = brandName.text.toString()
            if (itemBrandUnput.isEmpty()){
                itemBrandUnput = ""
            }
            val expirationDateString = expiryDate.text.toString()
            val chipId = chipGroup.checkedChipId
            var quantityInput = quantityText.text.toString().toInt()
            var error: Boolean = false

            if (itemNameInput.isEmpty()){
                itemName.setHint("Enter Item Name")
                itemName.setHintTextColor(Color.RED)
                error = true
            }
            // check if expiration date is not set
            if (expirationDateString.isEmpty()) {
                expiryDate.setHint(R.string.errorExpirySelection)
                expiryDate.setHintTextColor(Color.RED)
//                errorNoExpirationDate.visibility = View.VISIBLE
               error = true
            }

            // check if storage location is not set
            if (chipId == R.id.id_add_location_chip) {
                errorNoStorageLocation.setText(R.string.errorStorageSelection)
                errorNoStorageLocation.setTextColor(Color.RED)
                errorNoStorageLocation.visibility = View.VISIBLE
                error = true

            }

            if( error == true){
                return@setOnClickListener
            }

            // no error

            // get value from chip
            val chipChosen: Chip = findViewById(chipId)
            var storageChoice = chipChosen.text.toString()

            // get value from the spinner

            val formatter = SimpleDateFormat(stringPatternEditText, Locale.getDefault() )
//            val dateParsed = formatter.parse(expirationDateString.toString())
//
            val simpleDateFormat = SimpleDateFormat(timestampPatternFirebase, Locale.getDefault())
//            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
//            val timestamp = simpleDateFormat.parse(expirationDateString.toString())
//            val timestampExpiration = Timestamp(timestamp)

            val date: Date = formatter.parse(expirationDateString)
            val timestampExpiration = Timestamp(date)
            var currUser: User
            var item = Item(
                    itemNameInput,
                    itemBrandUnput,
                    quantityInput,
                    unitChosen,
                    timestampExpiration,
                    storageChoice
                    )

            db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { doc ->
                currUser = doc.toObject(User::class.java)!!
                // get reference to pantry
                val pantryReference: DocumentReference? =  currUser?.myPantry


                pantryReference?.collection("my_pantry")?.add(item)


            }

        }
    }


    private fun findViews(){
        // find views in layout
        unitSpinner = findViewById(R.id.add_unit_dropdown)
        calendarBtn = findViewById(R.id.iv_calendar_btn)
        itemName = findViewById(R.id.et_item_name)
        brandName= findViewById(R.id.et_brand)
        notes= findViewById(R.id.et_food_notes)
        expiryDate = findViewById(R.id.et_date_expiry)
        closeButton = findViewById(R.id.iv_exit_add_task)
        submitButton = findViewById(R.id.btn_add_new_item)
        quantityText = findViewById(R.id.et_quantity)
        addLocationChip =findViewById(R.id.id_add_location_chip)
        errorNoStorageLocation = findViewById(R.id.error_storage_location)
        errorNoExpirationDate = findViewById(R.id.error_expriation_date)


    }


//    https://material.io/components/chips#types
}

//TODO  check if the global list of storage location is empty, otherwise update