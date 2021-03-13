 package dev.kallytang.chomp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chomp.models.User
import kotlinx.android.synthetic.main.dialog_add_storage_location.view.*

class AddStorageLocationDialogue(private val addStorageName : AddNewStorageName) : DialogFragment() {
//    private lateinit var storageNames: ArrayList<String>
    private final var STORAGE_STRING = "storage_locations"
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    lateinit var pantryRef:DocumentReference
    lateinit var locationList: ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val rootView: View = inflater.inflate(R.layout.dialog_add_storage_location, container, false)
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { snapshot ->
            var user = snapshot.toObject(User::class.java)
            pantryRef = user?.myPantry!!

            pantryRef.get()
                ?.addOnSuccessListener { pantryDoc ->
                    val location: Map<String, String> =
                        pantryDoc.get("storage_locations") as Map<String, String>
                        locationList = arrayListOf()
                        locationList.addAll(location.keys)

                }
        }

        rootView.btn_cancel.setOnClickListener{
            dismiss()
        }
        rootView.iv_close_dialog.setOnClickListener{
            dismiss()
        }
        rootView.btn_add_name.setOnClickListener{
            rootView.btn_add_name.isEnabled = false

            //check if entry is empty
            if(rootView.et_add_new_storage_name.text.toString().trim().isNullOrBlank()){
                rootView.et_add_new_storage_name.setBackgroundResource(R.drawable.text_input_layout_red)
                rootView.et_add_new_storage_name.setHintTextColor(Color.RED)
                rootView.et_add_new_storage_name.setText("Please Enter a Storage Name")
                rootView.tv_error_message.visibility = View.VISIBLE
                rootView.btn_add_name.isEnabled = true
            }else{
//                pantryRef.collection("my_pantry").document().update()
                //check if pantry list is available
                var stringInput = rootView.et_add_new_storage_name.text.toString().trim()
                if(stringInput in locationList){
                    rootView.et_add_new_storage_name.setBackgroundResource(R.drawable.text_input_layout_red)
                    rootView.et_add_new_storage_name.setHintTextColor(Color.RED)
                    rootView.tv_error_message.setText("${stringInput} already exists, try again")
                    rootView.tv_error_message.visibility = View.VISIBLE
                    rootView.btn_add_name.isEnabled = true
                }else{
                    // add new storage location to the database
                    var map = mapOf(STORAGE_STRING to mapOf(stringInput.toLowerCase() to stringInput))
                    pantryRef.set(map, SetOptions.merge())
                    Toast.makeText(context, "Successfully added ${stringInput} to storage list", Toast.LENGTH_SHORT).show()
                    rootView.et_add_new_storage_name.text.clear()
                    rootView.tv_error_message.visibility = View.INVISIBLE
                    rootView.et_add_new_storage_name.setBackgroundResource(R.drawable.text_input_layout)
                    rootView.et_add_new_storage_name.setHintTextColor(Color.BLACK)
                    rootView.et_add_new_storage_name.setHint(R.string.e_g_cupboard)
                    addStorageName.addNewStorageName(stringInput)
                    rootView.btn_add_name.isEnabled = true
                }
            }
        }
        return rootView
    }

}