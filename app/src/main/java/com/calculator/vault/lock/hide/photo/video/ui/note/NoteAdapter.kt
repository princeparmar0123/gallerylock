package com.calculator.vault.lock.hide.photo.video.ui.note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.OnLongClickListener
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Note_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ItemNoteBinding
import com.calculator.vault.lock.hide.photo.video.ui.note.SecretNoteActivity.Companion.floating_btn_note
import com.calculator.vault.lock.hide.photo.video.ui.note.SecretNoteActivity.Companion.floating_del_note
import com.calculator.vault.lock.hide.photo.video.ui.note.SecretNoteActivity.Companion.select_note_data
import com.calculator.vault.lock.hide.photo.video.ui.note.addnote.AddNoteActivity


class NoteAdapter(var context: Context, var activity: Activity) :
    BaseAdapter<ItemNoteBinding, Note_Data>(
        R.layout.item_note
    ) {


    private var formatted: String? = null
    var IsLongClick = false

    override fun setClickableView(binding: ItemNoteBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(
        binding: ItemNoteBinding,
        position: Int,
        item: Note_Data,
        payloads: MutableList<Any>?
    ) {
        binding.run {


            adLayout.visibility = View.GONE
            adcard.visibility = View.GONE


            noteBack.setOnLongClickListener(OnLongClickListener {
                if (!IsLongClick) {
                    IsLongClick = true
                    addRemoveSelectionList(binding, position, item)
                }
                true
            })
            noteBack.setOnClickListener(View.OnClickListener { v: View? ->
                if (IsLongClick) {
                    addRemoveSelectionList(binding, position, item)
                } else {
                    val intent = Intent(activity, AddNoteActivity::class.java)
                    intent.putExtra("type", 1)
                    intent.putExtra("data", item)
                    activity.startActivity(intent)
                }
            })
            noteTitle.text = item.title
            noteDate.text = "Date: " + item.date
            if (select_note_data.contains(item)) {
                noteSelect.visibility = View.VISIBLE
            } else {
                noteSelect.visibility = View.GONE
            }

        }


    }

    fun addRemoveSelectionList(
        holder: ItemNoteBinding,
        position: Int,
        item: Note_Data,
    ) {
        try {
            holder.noteSelect.visibility = View.VISIBLE
            floating_btn_note?.visibility = View.GONE
            floating_del_note?.visibility = View.VISIBLE
            SecretNoteActivity.selecter?.isVisible = true
            if (select_note_data.contains(item)) {
                select_note_data.remove(item)
                holder.noteSelect.visibility = View.GONE
            } else {
                select_note_data.add(item)
                holder.noteSelect.visibility = View.VISIBLE
            }
            if (select_note_data.size == 0) {
                IsLongClick = false
                notifyItemChanged(position)
                floating_btn_note?.visibility = View.VISIBLE
                floating_del_note?.visibility = View.GONE
                SecretNoteActivity.selecter?.isVisible = false
                SecretNoteActivity.IsSelectAll = false
                SecretNoteActivity.selecter?.icon =
                    activity.resources.getDrawable(R.drawable.iv_unselect)
            }
            if (select_note_data.size == displayList.size) {
                SecretNoteActivity.IsSelectAll = true
                SecretNoteActivity.selecter?.icon =
                    activity.resources.getDrawable(R.drawable.iv_select)
            } else {
                SecretNoteActivity.IsSelectAll = false
                SecretNoteActivity.selecter?.icon =
                    activity.resources.getDrawable(R.drawable.iv_unselect)
            }
        } catch (e: Exception) {
        }
    }


}
