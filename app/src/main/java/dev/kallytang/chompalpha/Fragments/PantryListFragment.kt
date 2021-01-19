package dev.kallytang.chompalpha.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kallytang.chompalpha.AddFoodItemActivity
import dev.kallytang.chompalpha.MyApplication
import dev.kallytang.chompalpha.R
import dev.kallytang.chompalpha.models.Units
import dev.kallytang.chompalpha.models.User
import kotlinx.android.synthetic.main.fragment_pantry_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [PantryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PantryListFragment : Fragment() {
    private val db = Firebase.firestore
    private var unitsList = mutableListOf<Units>()
    private var unitsStrings = mutableListOf<String>()
    private var storageLocationList = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pantry_list, container, false)
        val fab: View = view.findViewById(R.id.fab_add_pantry_item)
        fab.setOnClickListener {
            val intent = Intent(context, AddFoodItemActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun getUnitData(){
        val applicationContext = context
        unitsList = ((applicationContext as MyApplication).unitsList as MutableList<Units>?)!!
        unitsStrings = (applicationContext as MyApplication).unitsAsString!!

        // check if pantry list already initialized, if not, set the data
        if ((applicationContext as MyApplication).storageLocationList == null){
            db.collection("users").document(auth.currentUser?.uid.toString()).get()
                .addOnSuccessListener { doc ->
                    (applicationContext as MyApplication).currUser = doc.toObject(User::class.java)

                    val pantryRef: DocumentReference? = (applicationContext as MyApplication).currUser?.myPantry
                        pantryRef?.get()
                        ?.addOnSuccessListener { pantryDoc ->
                            val location:Map<String, String> = pantryDoc.get("storage_locations") as Map<String, String>
                            val listLocation = ArrayList(location.keys)
                            listLocation.sort()
                            val locationStrings = listLocation.toMutableList()
                            (applicationContext as MyApplication).storageLocationList= locationStrings
                            storageLocationList = locationStrings
//                            Log.i("location", (applicationContext as MyApplication).storageLocationList.toString())
                            (applicationContext as MyApplication).pantryRef = pantryRef

                        }
                }
            // if the list exists in the application context
        }else{
            storageLocationList = (applicationContext as MyApplication).storageLocationList!!

        }

    }
}