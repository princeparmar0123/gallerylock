package com.calculator.vault.lock.hide.photo.video.common.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserLocal {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    var displayAlias: String? = null
    var displayPic: String? = null
}