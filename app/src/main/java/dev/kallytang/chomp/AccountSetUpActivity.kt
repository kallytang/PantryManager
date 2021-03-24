package dev.kallytang.chomp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chomp.databinding.ActivityAccountSetUpBinding


class AccountSetUpActivity : AppCompatActivity() {
    private companion object {
        private const val TAG = "AccountSetUpActivity"
        private const val PANTRIES = "pantries"
    }

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var binding: ActivityAccountSetUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSetUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_account_set_up)
        //initialize Firebase Auth
        auth = Firebase.auth
        //TODO: allowing users to invite members
        // TODO: allowing users to join a group

        val storageLocationNames = hashMapOf(
            "freezer" to "Freezer",
            "fridge" to "Fridge",
            "cabinet" to "Cabinet",
            "cupboard" to "Cupboard",
            "other" to "Other"
        )
//         add user list to pantries document
//        val listStorageNames = ArrayList(storageLocationNames.values)
//        listStorageNames.sort()
//        (applicationContext as MyApplication).addInitialStorageLocations(listStorageNames)

//

        // set up a user without adding a group or inviting group
        val currUser = auth.currentUser?.uid.toString()
        val pantry_ref = db.collection("pantries").document()


            val userInfo = hashMapOf(
                "display_name" to auth.currentUser?.displayName.toString(),
                "email" to auth.currentUser?.email.toString(),
                "my_pantry" to pantry_ref
            )
            val user_map = hashMapOf(
                currUser to auth.currentUser?.displayName.toString()
            )


            db.collection("users").document(currUser).set(
                userInfo
            ).addOnCompleteListener {
                pantry_ref.set(
                    hashMapOf(
                        "users_list" to user_map,
                        "storage_locations" to storageLocationNames
                    )).addOnSuccessListener { task ->
                    // when pantries document is created, add it to user as a reference
                    (applicationContext as MyApplication).updatePantryReference(pantry_ref)

                    Handler(Looper.getMainLooper()).postDelayed({
                        goToMain()
                    }, 3000)


                }

            }
            // add storage locations





//        goToMain()
    }


    // function to direct user to main activity
    private fun goToMain() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
//     function to set user account

//
//    private fun setUserAccount() {
//
//    }

}