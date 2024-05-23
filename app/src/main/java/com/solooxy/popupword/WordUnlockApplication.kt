package com.solooxy.popupword

import android.app.Application
import androidx.room.Room
import com.solooxy.popupword.db.AppDatabase
import com.solooxy.popupword.models.JsonFileItem
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WordUnlockApplication: Application() {
    var dataList: List<JsonFileItem> = emptyList()
    lateinit var db: AppDatabase
    companion object {
        //lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // 在这里进行全局初始化
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database.db"
        ).build()
    }
}