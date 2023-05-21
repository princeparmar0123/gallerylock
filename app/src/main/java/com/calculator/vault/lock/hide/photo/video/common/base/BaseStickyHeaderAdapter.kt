package com.calculator.vault.lock.hide.photo.video.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.common.stickyheader.StickyHeaderAdapter


/**
 * Prerequisite is that we should have our list sorted alphabetically by name
 *
 * Here is the logic behind sticky headers
 * For ex:-
 * Sorted List:    ["Aaron", "Amily" , "Anthony", "Bob", "Cheryl"]
 * Headers:        ["A", "A", "A", "B", "C"]
 * HeadersIds:     [0L,  0L,  0L,  1L,  2L]
 * HeaderPosition: [0,              1,   2]
 * */

abstract class BaseStickyHeaderAdapter<SVB : ViewDataBinding, VB : ViewDataBinding, T>(
    private val headerLayout: Int,
    itemLayout: Int
) : BaseFilterAdapter<VB, T>(itemLayout),
    StickyHeaderAdapter<BaseStickyHeaderAdapter.StickyHeaderHolder<SVB>> {

    override fun getHeaderId(position: Int): Long = getMyHeaderId(position)

    override fun onCreateHeaderViewHolder(parent: ViewGroup): StickyHeaderHolder<SVB> =
        StickyHeaderHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                headerLayout,
                parent,
                false
            )
        )

    override fun onBindHeaderViewHolder(viewHolder: StickyHeaderHolder<SVB>, position: Int) {
        onBindHeader(viewHolder.binding, position)
    }

    abstract fun getMyHeaderId(position: Int): Long

    abstract fun onBindHeader(headerBinding: SVB, position: Int)

    class StickyHeaderHolder<SVB : ViewDataBinding>(val binding: SVB) : RecyclerView.ViewHolder(binding.root)
}
