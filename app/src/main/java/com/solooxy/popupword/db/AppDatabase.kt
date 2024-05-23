package com.solooxy.popupword.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.solooxy.popupword.interfaces.FavoriteItemDao
import com.solooxy.popupword.models.FavoriteItem

@Database(entities = [FavoriteItem::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteItemDao
}