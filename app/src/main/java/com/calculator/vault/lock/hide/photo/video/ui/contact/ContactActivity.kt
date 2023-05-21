package com.calculator.vault.lock.hide.photo.video.ui.contact

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Contact_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityContactBinding

class ContactActivity : BaseActivity<ActivityContactBinding>(R.layout.activity_contact),
    View.OnClickListener {


    var contact_datas: ArrayList<Contact_Data> = ArrayList()
    var filter: ArrayList<Contact_Data> = ArrayList()
    var select_contact_datas = ArrayList<Contact_Data>()
    private var contact_adapter: ContactAdapter? = null
    private var menu_search: MenuItem? = null
    private var searchView: SearchView? = null
    private var database: Database? = null

    companion object {
        var select_contact_datas = ArrayList<Contact_Data>()
        var selecter: MenuItem? = null
        var IsSelectAll = false
        var floating_btn_contact: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? =
            null
        var floating_del_contact: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? =
            null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Contact"
        binding.contactToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.contactToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        floating_btn_contact = binding.floatingBtnContact
        floating_del_contact = binding.floatingDelContact
        onClickListener()
        initView()
        initAdapter(contact_datas)
    }


    private fun onClickListener() {
        binding.floatingBtnContact.setOnClickListener(this)
    }


    private fun initAdapter(data: ArrayList<Contact_Data>) {
        binding.run {
            if (contact_datas.size > 0) {
                binding.contactRecycler.visibility = View.VISIBLE
                noContact.visibility = View.GONE
                if (menu_search != null) {
                    menu_search?.isVisible = true
                }
                contact_adapter = ContactAdapter(this@ContactActivity, this@ContactActivity)
                binding.contactRecycler.layoutManager =
                    LinearLayoutManager(this@ContactActivity, LinearLayoutManager.VERTICAL, false)
                contactRecycler.adapter = contact_adapter
                contact_adapter?.addAll(data)
            } else {
                contactRecycler.visibility = View.GONE
                noContact.visibility = View.VISIBLE
                floatingDelContact.visibility = View.GONE
                floatingBtnContact.visibility = View.VISIBLE
                if (menu_search != null) {
                    menu_search?.isVisible = false
                } else {
                }
            }
        }

    }

    private fun initView() {
        contact_datas.clear()
        database = Database(this@ContactActivity)
        database?.allContact?.let { contact_datas.addAll(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.select -> if (contact_datas.size > 0) {
                if (!IsSelectAll) {
                    IsSelectAll = true
                    contact_adapter?.IsLongClick = true
                    item.icon = resources.getDrawable(R.drawable.iv_select)
                    select_contact_datas.clear()
                    select_contact_datas.addAll(contact_datas)
                    binding.floatingBtnContact.visibility = View.GONE
                    binding.floatingDelContact.visibility = View.VISIBLE
                } else {
                    IsSelectAll = false
                    contact_adapter?.IsLongClick = false
                    item.icon = resources.getDrawable(R.drawable.iv_unselect)
                    select_contact_datas.clear()
                    binding.floatingBtnContact.visibility = View.VISIBLE
                    binding.floatingBtnContact.visibility = View.GONE
                    selecter?.isVisible = false
                }
                contact_adapter?.notifyDataSetChanged()
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        selecter = menu.findItem(R.id.select)
        menu_search = menu.findItem(R.id.menu_search)
        selecter?.isVisible = false
        menu_search?.isVisible = contact_datas.size > 0
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView?.queryHint = "Search Contact"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!newText.isEmpty()) {
                    filter.clear()
                    for (i in contact_datas.indices) {
                        if (contact_datas[i].name.contains(newText)) {
                            filter.add(contact_datas[i])
                        }
                    }
                    initAdapter(filter)
                } else {
                    contact_datas.clear()
                    database?.allContact?.let { contact_datas.addAll(it) }
                    initAdapter(contact_datas)
                }
                return true
            }
        })
        return true
    }

    override fun onResume() {
        super.onResume()
        contact_datas.clear()
        database?.allContact?.let { contact_datas.addAll(it) }
        initAdapter(contact_datas)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floating_btn_contact -> {
                searchView?.isIconified = true
                searchView?.onActionViewCollapsed()
                val intent = Intent(this, AddContactActivity::class.java)
                intent.putExtra("type", 0)
                startActivity(intent)
            }
            R.id.floating_del_contact -> {
                val deletedialog = AlertDialog.Builder(this@ContactActivity)
                    .setTitle("Delete")
                    .setMessage("Are you sure,you want to delete this contacts?")
                    .setPositiveButton("Delete") { dialog, whichButton ->
                        if (select_contact_datas.size > 0) {
                            for (i in select_contact_datas.indices) {
                                database?.deleteContact(select_contact_datas[i].id)
                            }
                            contact_datas.clear()
                            database?.allContact?.let { contact_datas.addAll(it) }
                            initAdapter(contact_datas)
                            contact_adapter?.IsLongClick = false
                            IsSelectAll = false
                            select_contact_datas.clear()
                            binding.floatingDelContact.visibility = View.GONE
                            binding.floatingBtnContact.visibility = View.VISIBLE
                            selecter?.isVisible = false
                            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "cancel"
                    ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                    .create()
                deletedialog.show()
            }
        }
    }
}