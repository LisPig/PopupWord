package com.example.wordunlock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R
import com.example.wordunlock.models.JsonFileItem

class HomeAdapter(/*private val onItemSelected: (Int, Boolean?) -> Unit,
                  private val selectedPositions: MutableSet<Int>,*/
                  private val fileNames: Array<String>? = null
) : RecyclerView.Adapter<HomeViewHolder>() {

    // 假设您有一个数据列表
    private var dataList: List<JsonFileItem> = emptyList()
    init {
        dataList = fileNames?.map { fileName ->
            // 创建一个数据对象，包含文件名和初始的选中状态
            JsonFileItem(fileName, false)
        }!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_raw_file, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataList.size

}