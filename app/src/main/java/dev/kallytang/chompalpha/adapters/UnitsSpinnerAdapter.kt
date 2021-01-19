package dev.kallytang.chompalpha.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dev.kallytang.chompalpha.R
import dev.kallytang.chompalpha.models.Units

class UnitsSpinnerAdapter(var contextSpinner: Context, var resources: Int, val unitsList: ArrayList<Units>): ArrayAdapter<Units>(contextSpinner, resources, unitsList){


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setUpView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setUpView(position, convertView, parent)
    }

    private fun setUpView(position: Int, convertView: View?, parent: ViewGroup):View {
        var viewItem: View
        var unitName: TextView
        var unitItem: Units? = getItem(position)
        if (convertView == null){
            viewItem = LayoutInflater.from(context).inflate(
                R.layout.unit_spinner_row, parent, false
            )
            unitName = viewItem.findViewById(R.id.tv_unit)
            if (unitItem != null) {
                unitName.setText(unitItem.abbreviation)
            }
            return viewItem
        }else{
            unitName = convertView.findViewById(R.id.tv_unit)
            if (unitItem != null) {
                unitName.setText(unitItem.abbreviation)
            }
            return  convertView
        }
    }
}