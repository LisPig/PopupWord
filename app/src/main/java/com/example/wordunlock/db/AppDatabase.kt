package com.example.wordunlock.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wordunlock.interfaces.FavoriteItemDao
import com.example.wordunlock.models.FavoriteItem

@Database(entities = [FavoriteItem::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteItemDao
}