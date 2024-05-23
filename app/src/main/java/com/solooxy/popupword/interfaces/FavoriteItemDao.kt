package com.solooxy.popupword.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solooxy.popupword.models.FavoriteItem

@Dao
interface FavoriteItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FavoriteItem)

    @Query("SELECT * FROM favorite_items WHERE word LIKE :word")
    fun getFavoritesByWord(word: String): FavoriteItem

    // 其他查询、更新、删除操作

    @Query("SELECT * FROM favorite_items")
    suspend fun getAllFavorites(): List<FavoriteItem>

    @Query("DELETE  FROM favorite_items WHERE word LIKE :word")
    suspend fun delete(word: String)

    @Query("DELETE  FROM favorite_items WHERE id LIKE :id")
    suspend fun deleteById(id: Int)
}