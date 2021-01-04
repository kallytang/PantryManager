package dev.kallytang.chompalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity : AppCompatActivity() {
    private companion object{
        private const val TAG: String = "LoginActivity"
        private const val RC_GOOGLE_SIGN_IN = 1134
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

//        TODO set up email sign up another time
//        btn_login.setOnClickListener {
//            btn_login.isEnabled = false
//            var email = et_email.text.toString()
//            val password = et_password.text.toString()
//            if (email.isBlank() || password.isBlank()){
//                Toast.makeText(this, "Email/Password can't be empty", Toast.LENGTH_SHORT)
//                return@setOnClickListener
//            }
//            var authEmail = FirebaseAuth.getInstance()
//            authEmail.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
//                btn_login.isEnabled = true
//                if(task.isSuccessful){
//                    goToMainActivity()
//                }else{
//                    Log.i(TAG, "signing in failed", task.exception)
//                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        //initialize Firebase Auth
        auth = Firebase.auth



        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)
        btn_sign_in2.setOnClickListener {
            val signInIntent: Intent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }


    }

//    private fun goToMainActivity() {
//        Log.i(TAG, "going to main activity")
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)

            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    var result = task.result
                    var newUser = result?.getAdditionalUserInfo()?.isNewUser()
                    if (newUser== true){
                        //set up database for new user

                    }
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
//                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
                    Toast.makeText(this, "Authentication Failure", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        //go to main activity if signed in
        if(currentUser==null){
            Log.w(TAG, "User is null")
            return
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

