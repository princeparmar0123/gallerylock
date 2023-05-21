package com.calculator.vault.lock.hide.photo.video.ui

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.calculator.vault.lock.hide.photo.video.IntroActivity
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.databinding.ActivitySplashBinding
import org.jetbrains.anko.startActivity
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences: SharedPreferences = getSharedPreferences(
            Constant.SHARED_PREFS, MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putBoolean(
            Constant.SHARED_PREFS_AD_ON,
            false
        )
        editor.apply()

        nextActivity(3000)


    }

    fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private fun nextActivity(delay: Int) {
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity<IntroActivity>()
            finish()
        }, delay.toLong())
    }

}