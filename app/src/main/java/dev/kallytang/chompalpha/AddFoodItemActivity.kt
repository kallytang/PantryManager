package dev.kallytang.chompalpha

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.adapters.StorageSpinnerAdapter
import dev.kallytang.chompalpha.adapters.UnitsSpinnerAdapter
import dev.kallytang.chompalpha.databinding.ActivityAddFoodItemBinding
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class AddFoodItemActivity : AppCompatActivity() {
    private final val TAG = "AddFoodItemActivity"
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var datePicker: MaterialDatePicker.Builder<Long>
    private lateinit var materialDatePicker: MaterialDatePicker<Long>
    private val stringPatternEditText = "MMM d, yyyy"
    private val timestampPatternFirebase = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private lateinit var unitsList: ArrayList<Units>
    private lateinit var unitsSpinnerAdapter: UnitsSpinnerAdapter
    private lateinit var unitChosen: Units
    private lateinit var storageNames: ArrayList<String>
    private lateinit var storageSpinnerAdapter: StorageSpinnerAdapter
    private lateinit var photoFile: File
    private lateinit var binding: ActivityAddFoodItemBinding

    companion object {
        private final var REQUEST_CODE = 86
//        private final var RESULT_OK = 90
        private final var PHOTO_CODE = 311
        private val PERMISSION_CODE_GALLERY = 46;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_food_item)

        auth = Firebase.auth


        // set up calendar dialog
        datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Select an Expiration Date")
        materialDatePicker = datePicker.build()


        binding.etDateExpiry.setOnClickListener {
            binding.etDateExpiry.isEnabled = false
            materialDatePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        // for when user confirms date, convert date into a timestamp
        materialDatePicker.addOnPositiveButtonClickListener { date ->
            Log.i("date", materialDatePicker.headerText)
            Log.i("date", date.toString())
            binding.etDateExpiry.setText(materialDatePicker.headerText)
            binding.etDateExpiry.isEnabled = true
        }
        materialDatePicker.addOnDismissListener {
            binding.etDateExpiry.isEnabled = true
        }
        materialDatePicker.addOnCancelListener {
            binding.etDateExpiry.isEnabled = true
        }

        //set up storage location
        storageNames = ArrayList()
        (applicationContext as MyApplication).storageLocationList?.let { storageNames.addAll(it)}
        storageSpinnerAdapter = StorageSpinnerAdapter(this, R.layout.spinner_row, storageNames)
        binding.addLocationSpinner.adapter = storageSpinnerAdapter

        // set up units spinner
        unitsList = ArrayList()

        (applicationContext as MyApplication).unitsList?.let { unitsList.addAll(it) }

        var indexUnit = 0
        for (idx in unitsList.indices) {
            if (unitsList[idx].unitName == "none") {
                indexUnit = idx
            }
        }


        unitsSpinnerAdapter = UnitsSpinnerAdapter(this, R.layout.spinner_row, unitsList)
//        Log.i("unitsList",unitsList.toString())
        binding.addUnitSpinner.adapter = unitsSpinnerAdapter
        binding.addUnitSpinner.setSelection(indexUnit)


        var unitChoice: Units
        binding.addUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        var locationChosen = "Other"
        binding.addLocationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                locationChosen = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //for closing button
        binding.ivExitAddTask.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



        binding.fabCamera.setOnClickListener {
            var intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
//        TODO animate the mini fab
//        binding.fabCamera.setOnLongClickListener {
//            binding.fabCamera.isExpanded
//
//            // move the image fab button up
//
//            return@setOnLongClickListener true
//        }

        binding.fabGetFromGallery.setOnClickListener {

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_GALLERY)
                }else{
                    openPhotos()
                }
            }else{
                openPhotos()
            }


//
        }

        // submit button 
        binding.btnAddNewItem.setOnClickListener {
            val itemNameInput = binding.etItemName.text.toString()
            var itemBrandUnput = binding.etBrand.text.toString()
            if (itemBrandUnput.isEmpty()) {
                itemBrandUnput = ""
            }
            val expirationDateString = binding.etDateExpiry.text.toString()
            val quantityInput = binding.etQuantity.text.toString().toInt()
            var error: Boolean = false

            if (itemNameInput.isEmpty()) {
                binding.etItemName.setHint("Enter Item Name")
                binding.etItemName.setHintTextColor(Color.RED)
                binding.itemNameDot.visibility = View.VISIBLE
                binding.etItemName.setBackgroundResource(R.drawable.text_input_layout_red)
                error = true
            }
            // check if expiration date is not set
            if (expirationDateString.isEmpty()) {
                binding.etDateExpiry.setHint(R.string.errorExpirySelection)
                binding.etDateExpiry.setHintTextColor(Color.RED)
                binding.expirationDateDot.visibility = View.VISIBLE
                binding.etDateExpiry.setBackgroundResource(R.drawable.text_input_layout_red)
//                errorNoExpirationDate.visibility = View.VISIBLE
                error = true
            }


            if (error == true) {
                return@setOnClickListener
            }

            // get value from the spinner
            val formatter = SimpleDateFormat(stringPatternEditText, Locale.getDefault())
            val simpleDateFormat = SimpleDateFormat(timestampPatternFirebase, Locale.getDefault())


            val date: Date = formatter.parse(expirationDateString)
            val timestampExpiration = Timestamp(date)
            var currUser: User
            var item = Item(
                itemNameInput,
                itemBrandUnput,
                quantityInput,
                unitChosen,
                timestampExpiration,
                locationChosen
            )

            var pantryReference = (applicationContext as MyApplication).pantryRef
            pantryReference?.collection("my_pantry")?.add(item)
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }

    private fun openPhotos() {
        val imageSelectionIntent =  Intent(Intent.ACTION_GET_CONTENT)
        imageSelectionIntent.type = "image/*"
        if (imageSelectionIntent.resolveActivity(packageManager) != null){
            startActivityForResult(imageSelectionIntent, PHOTO_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null) {
                photoFile = data.extras?.get("photo") as File
                if (photoFile.exists()) {
                    binding.ivNewFoodImage.visibility = View.VISIBLE
                    Glide.with(this).load(photoFile).into(binding.ivNewFoodImage)
                }
            }
        }
        if(requestCode == PHOTO_CODE && resultCode == RESULT_OK){
            var photo = data?.data
            Log.i("photoData","photouri, $photo")
            if (photo !=null){
                binding.ivNewFoodImage.visibility = View.VISIBLE
                Glide.with(this).load(photo).into(binding.ivNewFoodImage)
            }

        }
    }



}

