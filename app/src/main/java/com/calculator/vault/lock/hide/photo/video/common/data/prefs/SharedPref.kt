package com.calculator.vault.lock.hide.photo.video.common.data.prefs

import android.content.Context
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.UserInfo
import com.google.gson.Gson

class SharedPref(context: Context, private val gson: Gson) : EncryptedPreferences(context) {

    private inline fun <reified T> toJson(value: T?) =
        if (value == null) null else gson.toJson(value)

    private inline fun <reified T> fromJson(value: String?) =
        if (value.isNullOrEmpty()) null else gson.fromJson(value, T::class.java)

    //<editor-fold desc="Clear App Data">
    fun clearAppUserData() {
        //TODO Clear your User Data here, when user logs out
    }
    //</editor-fold>

    var authToken: String?
        get() = getString(this::authToken.name)
        set(value) = setString(this::authToken.name, value)


    var userInfo: UserInfo?
        set(value) = setString(this::userInfo.name, toJson(value))
        get() = try {
            fromJson(getString(this::userInfo.name))
        } catch (e: Exception) {
            null
        }

    var mtUserId: String?
        get() = getString(this::mtUserId.name)
        set(value) = setString(this::mtUserId.name, value)


    var Pass: Boolean
        get() = getBoolean(this::Pass.name, false)
        set(value) = setBoolean(this::Pass.name, value)

    var password: String?
        get() = getString(this::password.name)
        set(value) = setString(this::password.name, value)

    var hiddenUri: String?
        get() = getString(this::hiddenUri.name)
        set(value) = setString(this::hiddenUri.name, value)

    var intruderUri: String?
        get() = getString(this::intruderUri.name)
        set(value) = setString(this::intruderUri.name, value)

    var question: Boolean
        get() = getBoolean(this::question.name, false)
        set(value) = setBoolean(this::question.name, value)

    var hiddenPermission: Boolean
        get() = getBoolean(this::hiddenPermission.name, false)
        set(value) = setBoolean(this::hiddenPermission.name, value)

    var isRecyclePermission: Boolean
        get() = getBoolean(this::isRecyclePermission.name, false)
        set(value) = setBoolean(this::isRecyclePermission.name, value)

    var getIntruder: Boolean
        get() = getBoolean(this::getIntruder.name, false)
        set(value) = setBoolean(this::getIntruder.name, value)

    var syncOn: Boolean
        get() = getBoolean(this::syncOn.name, false)
        set(value) = setBoolean(this::syncOn.name, value)

    var syncWifi: Boolean
        get() = getBoolean(this::syncWifi.name, false)
        set(value) = setBoolean(this::syncWifi.name, value)

    var setQuestion: Int
        get() = getInt(this::setQuestion.name)
        set(value) = setInt(this::setQuestion.name, value)

    var setAnswer: String?
        get() = getString(this::setAnswer.name)
        set(value) = setString(this::setAnswer.name, value)

    var recycleUri: String?
        get() = getString(this::recycleUri.name)
        set(value) = setString(this::recycleUri.name, value)

    var folderId: String?
        get() = getString(this::folderId.name)
        set(value) = setString(this::folderId.name, value)

    var imageFolder: String?
        get() = getString(this::imageFolder.name)
        set(value) = setString(this::imageFolder.name, value)

    var videoFolder: String?
        get() = getString(this::videoFolder.name)
        set(value) = setString(this::videoFolder.name, value)

    var clickImage: String?
        get() = getString(this::clickImage.name)
        set(value) = setString(this::clickImage.name, value)

    var recordVideo: String?
        get() = getString(this::recordVideo.name)
        set(value) = setString(this::recordVideo.name, value)


}