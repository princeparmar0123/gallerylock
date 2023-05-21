package com.calculator.vault.lock.hide.photo.video.ui.contact

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Contact_Data
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityAddContactBinding

class AddContactActivity : BaseActivity<ActivityAddContactBinding>(R.layout.activity_add_contact),View.OnClickListener {

    private var database: Database? = null
    private var type = 0
    private var contactdata: Contact_Data? = null
    private var display_name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Add Contact"
        binding.contactAddToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.contactAddToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = Database(this@AddContactActivity)
        onListener()
        type = intent.getIntExtra("type", 0)
        if (type == 1) {
            contactdata = intent.getSerializableExtra("data") as Contact_Data?
            binding.name.setText(contactdata!!.name)
                binding.number.setText(contactdata!!.number)
            binding.contactSave.text = "Update"
        }
        loadNative()
    }


    private fun onListener(){
        binding.contactSave.setOnClickListener(this)
    }

    private fun loadNative() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.contacts -> {
                val i = Intent(Intent.ACTION_PICK)
                i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                startActivityForResult(i, 110)
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.contact_save->{
                if (binding.name.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    if (binding.number.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        val contact_data = Contact_Data()
                        contact_data.name = binding.name.text.toString().trim { it <= ' ' }
                        contact_data.number = binding.number.text.toString().trim { it <= ' ' }
                        if (type == 0) {
                            database!!.addContact(contact_data)
                        } else if (type == 1) {
                            contact_data.id = contactdata!!.id
                            database!!.updateContact(contact_data)
                        }
                        finish()
                        //                        loadAds.showFullAd(Add_Contact_Activity.this);
                        //showInter()
                    } else {
                        Toast.makeText(this, "Enter Number...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contact, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 110 && resultCode == RESULT_OK) {
            val contactUri = data!!.data
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val cursor = contentResolver.query(contactUri!!, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val mobile = cursor.getString(numberIndex)
                display_name = cursor.getString(nameIndex)
                    binding.number.setText(mobile.trim { it <= ' ' })
                        binding.name.setText(display_name)
                cursor.close()
            }
        }
    }

}