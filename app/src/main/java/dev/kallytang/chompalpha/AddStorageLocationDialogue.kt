package dev.kallytang.chompalpha

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import dev.kallytang.chompalpha.databinding.DialogAddStorageLocationBinding

class AddStorageLocationDialogue : DialogFragment() {
    private lateinit var binding: DialogAddStorageLocationBinding
    private lateinit var list: MutableList<String>
    // empty constructor
//    fun AddStorageLocationDialogue(){
//
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.dialog_add_storage_location, null))
                .setPositiveButton(R.string.add,
                    DialogInterface.OnClickListener { dialog, which ->
                        Log.i("storageName", binding.etAddNewStorageName.text.toString())

                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Dialog can't be null")
    }
}