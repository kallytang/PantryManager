package dev.kallytang.chompalpha.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.kallytang.chompalpha.AddFoodItemActivity
import dev.kallytang.chompalpha.R
import kotlinx.android.synthetic.main.fragment_pantry_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [PantryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PantryListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pantry_list, container, false)

        fab_add_pantry_item.setOnClickListener { view ->
            var intent = Intent(context, AddFoodItemActivity::class.java)
            startActivity(intent)
        }

        return view
    }


}