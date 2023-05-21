package com.calculator.vault.lock.hide.photo.video.ui.note.addnote

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Note_Data
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : BaseActivity<com.calculator.vault.lock.hide.photo.video.databinding.ActivityAddNoteBinding>(R.layout.activity_add_note) {

    private val type = 0
    private var note_update: Note_Data? = null
    private var note_dialog: Dialog? = null
    private var database: Database? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.noteAddToolbar.title = "Add Note"
        binding.noteAddToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.noteAddToolbar)
        database = Database(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (type == 1) {
            note_update = intent.getSerializableExtra("data") as Note_Data?
            binding.note.setText(note_update!!.note)
        }
        loadNative()

        binding.cardSubmit.setOnClickListener {
            if (type == 0) {
                if (binding.note.getText().toString().trim { it <= ' ' }.length > 0) {
                    initDialog()
                } else {
                    Toast.makeText(this, "Enter Some Notes...", Toast.LENGTH_SHORT).show()
                }
            } else if (type == 1) {
                if (binding.note.getText().toString().trim { it <= ' ' }.length > 0) {
                    initDialog()
                } else {
                    Toast.makeText(this, "Enter Some Notes...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun loadNative() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.done -> if (type == 0) {
                if (binding.note.getText().toString().trim { it <= ' ' }.length > 0) {
                    initDialog()
                } else {
                    Toast.makeText(this, "Enter Some Notes...", Toast.LENGTH_SHORT).show()
                }
            } else if (type == 1) {
                if (binding.note.getText().toString().trim { it <= ' ' }.length > 0) {
                    initDialog()
                } else {
                    Toast.makeText(this, "Enter Some Notes...", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return false
    }

    private fun initDialog() {
        note_dialog = Dialog(this@AddNoteActivity, R.style.WideDialog)
        note_dialog?.setContentView(R.layout.dialog_title)
        note_dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val note_title = note_dialog?.findViewById<EditText>(R.id.note_title)
        if (type == 1) {
            note_title?.setText(note_update!!.title)
        }
        val cancel = note_dialog?.findViewById<Button>(R.id.cancel)
        val done = note_dialog?.findViewById<Button>(R.id.done)
        cancel?.setOnClickListener { note_dialog?.dismiss() }
        done?.setOnClickListener {
            if (note_title?.text.toString().isEmpty()) {
                Toast.makeText(this@AddNoteActivity, "Title can't be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (note_title?.text.toString()[0] == ' ') {
                Toast.makeText(
                    this,
                    "First letter of title can't be Space",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (type == 0) {
                    val note_data = Note_Data()
                    note_data.title = note_title?.text.toString().trim { it <= ' ' }
                    note_data.note = binding.note.getText().toString().trim { it <= ' ' }
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    note_data.date = dateFormat.format(calendar.time)
                    database?.addNote(note_data)
                    finish()
                   // showInter()
                } else if (type == 1) {
                    val note_data = Note_Data()
                    note_data.id = note_update?.id
                    note_data.title = note_title?.text.toString().trim { it <= ' ' }
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    note_data.date = dateFormat.format(calendar.time)
                    note_data.note = binding.note.getText().toString().trim { it <= ' ' }
                    database?.updateNote(note_data)
                    finish()
                }
            }
        }
        note_dialog?.show()
        note_dialog?.setCancelable(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }
}