package com.calculator.vault.lock.hide.photo.video

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import coil.ImageLoader
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.util.CoilUtils
import com.calculator.vault.lock.hide.photo.video.common.data.database.AppDatabase
import com.calculator.vault.lock.hide.photo.video.common.data.database.daos.AppDao
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.SharedPref
import com.google.gson.Gson
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.multilanguage.LocaleManager
import com.onesignal.OneSignal
import okhttp3.OkHttpClient
import timber.log.Timber

class App : Application() {

    private lateinit var pref: SharedPref
    private lateinit var dao: AppDao
    var preferences: SharedPreferences? = null
    var prefEditor: SharedPreferences.Editor? = null

    companion object {
        private lateinit var mInstance: App

        @Synchronized
        fun getInstance(): App = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences("vs_app_app", MODE_PRIVATE)
        prefEditor = preferences?.edit()
        prefEditor?.commit()
        mInstance = this
        if (BuildConfig.DEBUG) Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
            }
        })

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId("94c83ceb-b6c8-402a-800a-af3a2cb32406")

        ImageLoader.Builder(this)
            .componentRegistry {
                add(VideoFrameFileFetcher(this@App))
                add(VideoFrameUriFetcher(this@App))
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(this@App))
                    .build()
            }
            .build()

        pref = SharedPref(this, Gson())
        dao = AppDatabase.getInstance(applicationContext).getDao()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    fun putRate(rate: Boolean) {
        prefEditor?.putBoolean("rate", rate)
        prefEditor?.commit()
    }



    public fun getRate(): Boolean {
        return preferences?.getBoolean("rate", false) == true
    }

    fun getPref() = pref

    fun getDao() = dao
}