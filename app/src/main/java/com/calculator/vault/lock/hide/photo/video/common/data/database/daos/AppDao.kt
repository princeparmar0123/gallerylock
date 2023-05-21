package com.calculator.vault.lock.hide.photo.video.common.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.calculator.vault.lock.hide.photo.video.common.data.database.entities.UserLocal

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMTUser(vararg items: UserLocal):List<Long>

    @Update
    suspend fun updateMTUser(item: UserLocal)

    @Delete
    suspend fun deleteMTUser(item: UserLocal)

    @Query("SELECT * FROM UserLocal")
    fun getMTUser(): LiveData<List<UserLocal>>
}