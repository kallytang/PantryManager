package dev.kallytang.chompalpha

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.kallytang.chompalpha.adapters.StorageSpinnerAdapter
import dev.kallytang.chompalpha.adapters.UnitSpinnerAdapter
import dev.kallytang.chompalpha.databinding.ActivityAddFoodItemBinding
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Unit
import dev.kallytang.chompalpha.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddFoodItemActivity : AppCompatActivity() {
    private final val TAG = "AddFoodItemActivity"
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var datePicker: MaterialDatePicker.Builder<Long>
    private lateinit var materialDatePicker: MaterialDatePicker<Long>
    private val stringPatternEditText = "MMM d, yyyy"
    private val timestampPatternFirebase = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private lateinit var unitList: ArrayList<Unit>
    private lateinit var unitSpinnerAdapter: UnitSpinnerAdapter
    private lateinit var unitChosen: Unit
    private lateinit var storageNames: ArrayList<String>
    private lateinit var storageSpinnerAdapter: StorageSpinnerAdapter
    private var photoFile: Uri? = null
    private lateinit var binding: ActivityAddFoodItemBinding
    private lateinit var storageRef: StorageReference

    companion object {
        private final var REQUEST_CODE = 86

        //        private final var RESULT_OK = 90
        private final var PHOTO_CODE = 311
        private val PERMISSION_CODE_GALLERY = 46;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_food_item)
        storageRef = FirebaseStorage.getInstance().getReference();
        auth = Firebase.auth


        // set up calendar dialog
        datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Select an Expiration Date")
        materialDatePicker = datePicker.build()

        // for opening calendar dialog
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
//        if((applicationContext as MyApplication).storageLocationList.isNullOrEmpty()){
//            storageNames.addAll((applicationContext as MyApplication).getQueryStorageLocations())
//        }

        (applicationContext as MyApplication).storageLocationList?.let { storageNames.addAll(it) }
        storageSpinnerAdapter = StorageSpinnerAdapter(this, R.layout.spinner_row, storageNames)
        binding.addLocationSpinner.adapter = storageSpinnerAdapter

        // set up units spinner
        unitList = ArrayList()

        (applicationContext as MyApplication).unitList?.let { unitList.addAll(it) }

        var indexUnit = 0
        for (idx in unitList.indices) {
            if (unitList[idx].unitName == "none") {
                indexUnit = idx
            }
        }


        unitSpinnerAdapter = UnitSpinnerAdapter(this, R.layout.spinner_row, unitList)
//        Log.i("unitsList",unitsList.toString())
        binding.addUnitSpinner.adapter = unitSpinnerAdapter
        binding.addUnitSpinner.setSelection(indexUnit)


        var unitChoice: Unit
        binding.addUnitSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    unitChosen = parent?.getItemAtPosition(position) as Unit
//                var clickedUnitName:String = unitChosen.abbreviation
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        // for keeping track of what user chose as storage location
        var locationChosen = "Other"
        binding.addLocationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_GALLERY)
                } else {
                    openPhotos()
                }
            } else {
                openPhotos()
            }
        }

        // submit button 
        binding.btnAddNewItem.setOnClickListener {
            val itemNameInput = binding.etItemName.text.toString()
            var itemBrandUnput = binding.etBrand.text.toString()
            if (itemBrandUnput.isEmpty()) {
                itemBrandUnput = ""
            }
            var itemNotesInput = binding.etFoodNotes.text.toString()
            if (itemNotesInput.isEmpty()) {
                itemNotesInput = ""
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
            } else {
                binding.btnAddNewItem.isEnabled = false
                // get value from the spinner
                val formatter = SimpleDateFormat(stringPatternEditText, Locale.getDefault())
                val simpleDateFormat =
                    SimpleDateFormat(timestampPatternFirebase, Locale.getDefault())

                var item: Item

                if (photoFile != null) {
                    val userID = auth.currentUser?.uid.toString()
                    val photoRef =
                        storageRef.child("images/$userID/${System.currentTimeMillis()}_photo.jpg")
                    photoRef.putFile(photoFile!!)
                        .continueWithTask { photoUploadTask ->
                            photoRef.downloadUrl

                        }.continueWith { downloadUrl ->

                            val date: Date = formatter.parse(expirationDateString)
                            val timestampExpiration = Timestamp(date)
                            var currUser: User
                            item = Item(
                                itemNameInput,
                                itemBrandUnput,
                                quantityInput,
                                unitChosen,
                                timestampExpiration,
                                locationChosen,
                                itemNotesInput,
                                downloadUrl.result.toString(),
                                ""
                            )
                            val pantryReference = (applicationContext as MyApplication).pantryRef
                            pantryReference?.collection("my_pantry")?.add(item)
                        }.addOnCompleteListener{

                            binding.btnAddNewItem.isEnabled = false
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener{ e->
                            Log.i("addItem", e.toString())
                        }.addOnSuccessListener {
                            Log.i("addItem", it.toString())
                        }

                } else {
                    val imageUrl = ""
                    val date: Date = formatter.parse(expirationDateString)
                    val timestampExpiration = Timestamp(date)
                    var currUser: User
                    item = Item(
                        itemNameInput,
                        itemBrandUnput,
                        quantityInput,
                        unitChosen,
                        timestampExpiration,
                        locationChosen,
                        itemNotesInput,
                        imageUrl,
                        ""

                    )
                    val pantryReference = (applicationContext as MyApplication).pantryRef
                    pantryReference?.collection("my_pantry")?.add(item)
                    binding.btnAddNewItem.isEnabled = false
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

            }

        }
    }

    private fun openPhotos() {
        val imageSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageSelectionIntent.type = "image/*"
        if (imageSelectionIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(imageSelectionIntent, PHOTO_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                photoFile = data.extras?.get("photo") as Uri
                if (photoFile != null) {
                    binding.ivNewFoodImage.visibility = View.VISIBLE
                    Glide.with(this).load(photoFile).into(binding.ivNewFoodImage)
                }
            }
        }
        if (requestCode == PHOTO_CODE && resultCode == RESULT_OK) {
            photoFile = data?.data!!

            if (photoFile != null) {
                Log.i("photoData", "photouri, ${photoFile!!.javaClass.name}")
            }
            if (photoFile != null) {
                binding.ivNewFoodImage.visibility = View.VISIBLE
                Glide.with(this).load(photoFile).into(binding.ivNewFoodImage)
            }

        }
    }


}

