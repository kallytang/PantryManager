 package dev.kallytang.chompalpha

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.dialog_add_storage_location.view.*

class AddStorageLocationDialogue : DialogFragment() {
//    private lateinit var storageNames: ArrayList<String>
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    lateinit var pantryRef:DocumentReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_add_storage_location, container, false)
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { snapshot ->
            var user = snapshot.toObject(User::class.java)
            pantryRef = user?.myPantry!!

            pantryRef.get()
                ?.addOnSuccessListener { pantryDoc ->
                    val location: Map<String, String> =
                        pantryDoc.get("storage_locations") as Map<String, String>

                }
        }


        rootView.btn_cancel.setOnClickListener{
            dismiss()
        }
        rootView.btn_add_name.setOnClickListener{

            //check if entry is empty
            if(rootView.et_add_new_storage_name.text.isNullOrBlank()){
                rootView.et_add_new_storage_name.setBackgroundResource(R.drawable.text_input_layout_red)
                rootView.et_add_new_storage_name.setHintTextColor(Color.RED)
                rootView.et_add_new_storage_name.setText("Please Enter a Storage Name")
            }else{
//                pantryRef.collection("my_pantry").document().update()
            }



        }

        return rootView
    }

}