package dev.kallytang.chompalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_account_set_up.*

class AccountSetUpActivity : AppCompatActivity() {
    private companion object{
        private const val TAG = "AccountSetUpActivity"
        private const val PANTRIES = "pantries"
    }
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_set_up)
        //initialize Firebase Auth
        auth = Firebase.auth

        //TODO: allowing users to invite members
        // TODO: allowing users to join a group

        // set up a user without adding a group or inviting group
        setUserAccount()
    }

    // function to set user account
    private fun setUserAccount() {
        var currUser = auth.currentUser?.uid.toString()
        skip_group_setup_btn.setOnClickListener {
            var pantry_ref = db.collection("pantries").document()
            pantry_ref.set(
                hashMapOf(
                    currUser to auth.currentUser?.displayName.toString()
                )
            ).addOnSuccessListener { task ->
                db.collection("users").document(currUser).update(
                    "my_pantry", pantry_ref
                ).addOnFailureListener { exception ->
                    Log.i(TAG, "can't add it to the user? $exception")
                }
            }
            goToMain()
        }

    }

    fun goToMain(){
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
