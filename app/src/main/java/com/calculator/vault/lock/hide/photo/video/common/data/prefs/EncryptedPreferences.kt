package com.calculator.vault.lock.hide.photo.video.common.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

open class EncryptedPreferences(val context: Context) {

    private lateinit var iSharedPrefs: SharedPreferences

    init {
        try {
            val spec = KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()

            val masterKey = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build()

            iSharedPrefs = EncryptedSharedPreferences.create(
                context,
                "encrypted_prefs_hvc",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //region Boolean
    protected fun setBoolean(key: String, value: Boolean) =
        iSharedPrefs.edit().putBoolean(key, value).apply()

    protected fun getBoolean(key: String, defaultValue: Boolean) =
        iSharedPrefs.getBoolean(key, defaultValue)
    //endregion

    //region String
    protected fun setString(key: String, value: String?) =
        iSharedPrefs.edit().putString(key, value).apply()

    protected fun getString(key: String) = iSharedPrefs.getString(key, "")
    //endregion

    //region Integer
    protected fun setLong(key: String, value: Long) =
        iSharedPrefs.edit().putLong(key, value).apply()

    protected fun getLong(key: String) = iSharedPrefs.getLong(key, 0L)
    //endregion

    //region Integer
    protected fun setInt(key: String, value: Int) = iSharedPrefs.edit().putInt(key, value).apply()

    protected fun getInt(key: String) = iSharedPrefs.getInt(key, 0)
    //endregion


    //<editor-fold desc="Clear All Preferences">
    fun clearPreferences() {
        iSharedPrefs.edit().clear().apply()
    }
    //</editor-fold>
}