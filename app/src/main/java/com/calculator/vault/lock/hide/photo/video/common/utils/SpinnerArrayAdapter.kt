package com.calculator.vault.lock.hide.photo.video.common.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.databinding.ListItemSpinnerBinding
import java.util.*

class SpinnerArrayAdapter(
    private val con: Context,
    private val layoutId: Int,
    private val stringList: ArrayList<String>,
    private val isPromptedText: Boolean
) :
    ArrayAdapter<String>(con, layoutId, stringList) {
    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    private fun getCustomView(
        position: Int,
        parent: ViewGroup
    ): View {
        val binding: ListItemSpinnerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(con), layoutId, parent, false)

        // TODO 29/12/20 as per your design. hint may be gray.
//        if (isPromptedText && position == 0) {
//            binding.tvItemText.setTextColor(ContextCompat.getColor(con, R.color.colorWhiteOp60))
//        } else {
//            binding.tvItemText.setTextColor(ContextCompat.getColor(con, R.color.colorWhite))
//        }

        binding.tvItemText.text = stringList[position].capitalize(Locale.getDefault())
        return binding.root
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        super.getDropDownView(position, convertView, parent)
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }
}


fun Spinner.setUpSpinner(spinnerDataList: ArrayList<String>, promptText: String = "") {
    this.run {
        if (promptText.isNotEmpty()) {
            spinnerDataList.add(0, promptText)
        }
        val spinnerListAdapter = SpinnerArrayAdapter(
            context, R.layout.list_item_spinner,
            spinnerDataList, promptText.isNotEmpty()
        )
        this.adapter = spinnerListAdapter
    }
}

fun Spinner.getSelectedItem(isPromptedText: Boolean = false): Int {
    return if (isPromptedText) selectedItemPosition - 1 else selectedItemPosition
}

fun Spinner.isValid(isPromptedText: Boolean = false): Boolean {
    return if (isPromptedText) selectedItemPosition != 0 else true
}