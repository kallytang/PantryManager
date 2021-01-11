package dev.kallytang.chompalpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_pantry_list.*
import androidx.fragment.app.FragmentManager
import dev.kallytang.chompalpha.Fragments.PantryListFragment
import dev.kallytang.chompalpha.Fragments.RecipesFragment
import dev.kallytang.chompalpha.Fragments.ShoppingListFragment

class MainActivity : AppCompatActivity() {
    private companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavBar: BottomNavigationView
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for logging out
        auth = Firebase.auth
        //todo: make query to firestore to retrieve data

        // if the
        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                .replace(R.id.fl_containter_main, PantryListFragment())
                .commit()
        }

        // fragment management
        bottomNavBar = findViewById(R.id.bottom_navigation)

        bottomNavBar.setOnNavigationItemSelectedListener { item ->
            fragment = PantryListFragment()
            when (item.itemId){
                R.id.my_pantry_fragment -> {
                    fragment = PantryListFragment()
                }
                R.id.my_shopping_list_fragment -> {
                    fragment = ShoppingListFragment()
                }
                R.id.my_recipes_fragment -> {
                    fragment = RecipesFragment()
                }
                else -> {
                    fragment = PantryListFragment()
                }
            }
            fragmentManager.beginTransaction().replace(R.id.fl_containter_main, fragment).commit()
            return@setOnNavigationItemSelectedListener true

        }



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // TODO animate the menu button

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
}