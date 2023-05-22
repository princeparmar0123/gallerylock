package com.calculator.vault.lock.hide.photo.video.ui.note

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Note_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityNoteBinding
import com.calculator.vault.lock.hide.photo.video.ui.note.addnote.AddNoteActivity

class SecretNoteActivity : BaseActivity<ActivityNoteBinding>(R.layout.activity_note), View.OnClickListener {

    var noteAdapter: NoteAdapter? = null
    var note_data = ArrayList<Note_Data>()
    private var database: Database? = null

    companion object{
        var floating_btn_note: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? = null
        var floating_del_note: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? = null
        var select_note_data: ArrayList<Note_Data> = ArrayList()
        var IsSelectAll = false
        var selecter: MenuItem? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.noteToolbar.title = "Secret Note"

        binding.noteToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.noteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
          floating_del_note = binding.floatingDelNote
          floating_btn_note = binding.floatingBtnNote
        onClickListener()
        initView()
        initAdapter()
    }

    private fun onClickListener(){
        binding.run {
            floatingBtnNote.setOnClickListener(this@SecretNoteActivity)
            floating_del_note?.setOnClickListener(this@SecretNoteActivity)
        }
    }

    private fun initAdapter(){
        binding.run {
            if (note_data.size > 0) {
                noteRecycler.visibility = View.VISIBLE
                noNote.visibility = View.GONE
                noteAdapter = NoteAdapter(this@SecretNoteActivity, this@SecretNoteActivity)
                noteRecycler.layoutManager = LinearLayoutManager(this@SecretNoteActivity, LinearLayoutManager.VERTICAL, false)
                noteRecycler.adapter = noteAdapter
                noteAdapter?.addAll(note_data)
            } else {
                noteRecycler.visibility = View.GONE
                floatingDelNote.visibility = View.GONE
                floatingBtnNote.visibility = View.VISIBLE
                noNote.visibility = View.VISIBLE
            }
        }

    }

    private fun initView() {

        note_data.clear()
        database = Database(this)
        database?.allNote?.let { note_data.addAll(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.select -> if (note_data.size > 0) {
                if (!IsSelectAll) {
                    IsSelectAll = true
                    noteAdapter?.IsLongClick = true
                    item.icon = resources.getDrawable(R.drawable.iv_select)
                    select_note_data.clear()
                    select_note_data.addAll(note_data)
                    binding.floatingBtnNote.visibility = View.GONE
                    binding.floatingDelNote.visibility = View.VISIBLE
                } else {
                    IsSelectAll = false
                    noteAdapter?.IsLongClick = false
                    item.icon = resources.getDrawable(R.drawable.iv_unselect)
                   select_note_data.clear()
                    binding.floatingBtnNote.visibility = View.VISIBLE
                    binding.floatingDelNote.visibility = View.GONE
                    selecter?.isVisible = false
                }
                noteAdapter?.notifyDataSetChanged()
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (select_note_data.size != 0) {
            IsSelectAll = false
            noteAdapter?.IsLongClick = false
            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
            select_note_data.clear()
            binding.floatingBtnNote.visibility = View.VISIBLE
            binding.floatingDelNote.visibility = View.GONE
            selecter?.isVisible = false
            noteAdapter?.notifyDataSetChanged()
        } else {
            super.onBackPressed()
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_select, menu)
        selecter = menu.findItem(R.id.select)
        selecter?.isVisible = false
        return true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.floating_btn_note->{
                val intent = Intent(this, AddNoteActivity::class.java)
                intent.putExtra("type", 0)
                startActivity(intent)
            }
            R.id.floating_del_note->{
                val deletedialog = AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Are you sure,you want to delete this notes?")
                    .setPositiveButton("Delete") { dialog, whichButton ->
                        if (select_note_data.size > 0) {
                            for (i in select_note_data.indices) {
                                database!!.deleteNote(select_note_data.get(i).id)
                            }
                            note_data.clear()
                            database?.allNote?.let { note_data.addAll(it) }
                            initAdapter()
                            noteAdapter?.IsLongClick = false
                            IsSelectAll = false
                            select_note_data.clear()
                            binding.floatingDelNote.visibility = View.GONE
                            floating_btn_note?.visibility = View.VISIBLE
                            selecter?.isVisible = false
                            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "cancel"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    .create()
                deletedialog.show()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        note_data.clear()
        database?.getAllNote()?.let { note_data.addAll(it) }
        initAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        IsSelectAll = false
        noteAdapter?.IsLongClick = false
    }
}