package com.calculator.vault.lock.hide.photo.video.common.multilanguage

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.*


object LocaleManager {

    //Add your const language to support in app here
    const val ENGLISH = "en"
    const val GERMAN = "de"

    /**
     * SharedPreferences Key
     */
    private const val LANGUAGE_KEY = "language_key"

    /**
     * set current pref locale
     */
    fun setLocale(mContext: Context): Context {
        return updateResources(mContext, getLanguagePref(mContext))
    }

    /**
     * Set new Locale with context
     */
    fun setNewLocale(mContext: Context, language: String): Context {
        setLanguagePref(mContext, language)
        return updateResources(mContext, language)
    }

    /**
     * Get saved Locale from SharedPreferences
     *
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    fun getLanguagePref(mContext: Context?): String {
        val mPreferences = mContext?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        return mPreferences?.getString(LANGUAGE_KEY, Locale.getDefault().language)
            ?: Locale.getDefault().language
        //    return mPreferences.getString(LANGUAGE_KEY, ENGLISH);
    }

    /**
     * set pref key
     */
    private fun setLanguagePref(mContext: Context, localeKey: String) {
        val mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply()
    }

    /**
     * update resource
     */
    private fun updateResources(mContext: Context, language: String): Context {
        var context = mContext
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    /**
     * get current locale
     */
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales[0] else config.locale
    }
}