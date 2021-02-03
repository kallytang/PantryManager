package dev.kallytang.chompalpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import dev.kallytang.chompalpha.adapters.StorageSpinnerAdapter
import dev.kallytang.chompalpha.adapters.UnitSpinnerAdapter
import dev.kallytang.chompalpha.databinding.ActivityEditItemBinding
import dev.kallytang.chompalpha.models.Item
import dev.kallytang.chompalpha.models.Unit

class EditItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding
    private final val TAG = "EditItemActivity"
    private lateinit var unitList: ArrayList<Unit>
    private lateinit var storageList: ArrayList<String>
    private lateinit var spinnerStorageAdapter: StorageSpinnerAdapter
    private lateinit var spinnerUnitAdapter: UnitSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_item)
        val intent: Intent = intent
        val item: Item? = intent.getParcelableExtra("item")
//        val unit: Unit? = intent.getParcelableExtra("unit")

        //set up unitlist adapter
        unitList = ArrayList()
        (applicationContext as MyApplication).unitList?.let { unitList.addAll(it) }

//        Log.i("editItem", item.toString())
        Log.i("editItem", item.toString())

        var indexUnit = 0
        for (idx in unitList.indices) {

            if (item != null) {
                if (item.units?.unitName == unitList[idx].unitName) {
                    indexUnit = idx

                }
            }
        }

        spinnerUnitAdapter = UnitSpinnerAdapter(this, R.layout.spinner_row,
            unitList)

        binding.addUnitSpinner.adapter = spinnerUnitAdapter
        binding.addUnitSpinner.setSelection(indexUnit)

        // Storage list
        storageList = ArrayList()
        (applicationContext as MyApplication).storageLocationList?.let { storageList.addAll(it) }

        var indexStorage = 0
        for (idx in storageList.indices) {

            if (item?.location == storageList[idx]) {
                indexStorage = idx

            }
        }
        spinnerStorageAdapter = StorageSpinnerAdapter(this, R.layout.spinner_row, storageList)

        binding.addLocationSpinner.adapter = spinnerStorageAdapter
        binding.addLocationSpinner.setSelection(indexStorage)


        binding.ivExitEditTask.setOnClickListener{
            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }


        if (item != null) {
            if (item.imageUrl?.isNotEmpty() == true){
                Glide.with(this).load(item.imageUrl).into(binding.ivFoodPhoto)
                binding.ivFoodPhoto.visibility = View.VISIBLE
            }
            binding.tvInfoItemName.setText(item.name.toString())
            binding.etFoodNotes.setText(item.notes.toString())
            // convert the date to local time
//            binding.etDateExpiry.setText()
        }



    }


}