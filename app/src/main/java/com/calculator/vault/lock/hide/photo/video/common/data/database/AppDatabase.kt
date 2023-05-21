package com.calculator.vault.lock.hide.photo.video.common.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.calculator.vault.lock.hide.photo.video.common.data.database.daos.AppDao
import com.calculator.vault.lock.hide.photo.video.common.data.database.entities.UserLocal
import timber.log.Timber

@Database(entities = [UserLocal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): AppDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase")
                    .addCallback(roomCallback)
                    .build()
            }
            return instance!!
        }

        private var roomCallback: Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Timber.e("onCreate")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                Timber.e("onOpen")
            }
        }
    }
}