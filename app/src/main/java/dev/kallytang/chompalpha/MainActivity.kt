package dev.kallytang.chompalpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.google.firebase.firestore.ktx.firestore
import dev.kallytang.chompalpha.models.Units

class MainActivity : AppCompatActivity() {
    private companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getUnits()

        // removing shadow of action bar
        supportActionBar?.elevation = 0F;

        //for logging out
        auth = Firebase.auth
        //todo: make query to firestore to retrieve data

        val fab: View = findViewById(R.id.fab_add_pantry_item)
        fab.setOnClickListener {
            val intent = Intent(this, AddFoodItemActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_tab){
            Log.i(TAG, "Logged out")
            auth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }
        return  super.onOptionsItemSelected(item)
    }
    private fun getUnits(){
        var myApp = MyApplication()
        db.collection("units").get().addOnSuccessListener { snapshot ->
            Log.i("units", snapshot.toObjects(Units::class.java).toString())
            myApp.unitsList = snapshot.toObjects(Units::class.java)
        }
    }
}