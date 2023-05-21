package com.calculator.vault.lock.hide.photo.video.ui.passwords

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityPasswordBinding

class PasswordsActivity : BaseActivity<ActivityPasswordBinding>(R.layout.activity_password) {

    private var passwordAdapter: PasswordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Passwords"
        binding.passwordToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.passwordToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initAdapter()
        loadNative()
    }

    private fun loadNative() {

    }


    private fun initAdapter() {
        passwordAdapter =
            PasswordAdapter(
                this,
                Constant.password_name,
                Constant.password_icon
            )
        binding.passwordRecycler.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.passwordRecycler.adapter = passwordAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        passwordAdapter?.notifyDataSetChanged()
    }
}