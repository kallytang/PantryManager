package dev.kallytang.chompalpha

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import dev.kallytang.chompalpha.databinding.DialogAddStorageLocationBinding
import java.util.*

class AddStorageLocationDialogue:DialogFragment() {
    private lateinit var binding: DialogAddStorageLocationBinding

    // empty constructor
    fun AddStorageLocationDialogue(){

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }
}