package com.example.pantryappver1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity : AppCompatActivity() {
    private companion object{
        private const val TAG: String = "LoginActivity"
        private const val RC_GOOGLE_SIGN_IN = 1134
    }
    private lateinit var auth: FirebaseAuth
//
//    lateinit var etUsername: EditText
//    lateinit var etPassword: EditText
//    lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)
        sign_in_button.setOnClickListener {
            val signInIntent: Intent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }


    }
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

//private fun loginUser(username: String, password: String) {
//
//}

//private fun startMainActivity() {
//    var intent = Intent(this, MainActivity::class.java)
//    startActivity(intent)
//        etUsername = findViewById(R.id.et_username)
//        etPassword = findViewById(R.id.et_password)
//        btnLogin = findViewById(R.id.btn_login)
//        btnLogin.setOnClickListener {
//            var username: String = etUsername.text.toString()
//            var password: String = etPassword.text.toString()
////            loginUser(username, password)
//            Log.i(TAG, username + password)
//        }

//}