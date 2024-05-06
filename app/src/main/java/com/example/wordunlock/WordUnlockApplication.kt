package com.example.wordunlock

import android.app.Application
import com.example.wordunlock.models.JsonFileItem
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WordUnlockApplication: Application() {
    var dataList: List<JsonFileItem> = emptyList()
    override fun onCreate() {
        super.onCreate()
        // 在这里进行全局初始化

    }
}