package com.calculator.vault.lock.hide.photo.video.common.base

import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.ViewDataBinding

abstract class BaseFilterAdapter<VB : ViewDataBinding, T>(layout: Int) : BaseAdapter<VB, T>(layout), Filterable {

    /**
     * This is the main list where user can do search items from it.
     * */
    private val mainSearchList = ArrayList<T>()

    abstract fun includeItem(query: CharSequence?, item: T): Boolean

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if (constraint.isNullOrEmpty()) {
                    val filterResults = FilterResults()
                    filterResults.values = mainSearchList
                    filterResults.count = mainSearchList.size
                    return filterResults
                }

                val tempList = ArrayList<T>()
                mainSearchList.forEach {
                    val isExist = includeItem(constraint, it)
                    if (isExist) {
                        tempList.add(it)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = tempList
                filterResults.count = tempList.size

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                displayList.clear()
                displayList.addAll(results?.values as ArrayList<T>)
                notifyDataSetChanged()
            }
        }
    }

    fun getSearchList() =  mainSearchList

    override fun addAll(dataList: Collection<T>) {
        super.addAll(dataList)
        mainSearchList.clear()
        mainSearchList.addAll(dataList)
    }
}