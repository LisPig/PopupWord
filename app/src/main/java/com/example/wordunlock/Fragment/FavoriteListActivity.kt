package com.example.wordunlock.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R
import com.example.wordunlock.WordUnlockApplication
import com.example.wordunlock.adapters.FavoriteListAdapter
import com.example.wordunlock.db.AppDatabase
import com.example.wordunlock.models.FavoriteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteListAdapter
    private lateinit var db: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_recycler)

        db = (application as WordUnlockApplication).db
        recyclerView = findViewById(R.id.collection_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化适配器
        adapter = FavoriteListAdapter { item ->
            lifecycleScope.launch {
                // 在这里处理删除操作，例如调用DAO的delete方法
                item.id?.let { db.favoriteItemDao().deleteById(it) }
            }
        }
        recyclerView.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // 获取收藏列表数据并更新UI
        lifecycleScope.launch {
            val favoriteItems = withContext(Dispatchers.IO) {
                fetchFavoriteItemsFromDatabase().toList()
            }
            withContext(Dispatchers.Main.immediate) {
                adapter.submitList(favoriteItems)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // 关闭当前Activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun fetchFavoriteItemsFromDatabase(): List<FavoriteItem> {
        // 使用你的FavoriteItemDao获取数据
        return db.favoriteItemDao().getAllFavorites()
    }
}