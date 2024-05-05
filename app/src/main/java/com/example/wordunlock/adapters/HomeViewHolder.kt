package com.example.wordunlock.adapters

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R
import com.example.wordunlock.models.JsonFileItem


class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // 假设您有一个CheckBox来表示选中状态
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    private val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_text_view)

    // 新增方法用于绑定JSON文件名
    fun bindJsonFileName(fileNames: Array<String>?, isSelected: Boolean, onItemSelected: (Int, Boolean) -> Unit) {
        val fileName = fileNames?.get(position)
        fileNameTextView.text = fileName?.substringBeforeLast(".")
        //checkBox.visibility = View.GONE // 或者根据需求调整CheckBox的可见性
        // 无需设置选中状态或监听器，因为JSON项可能不需要选中功能
    }

    // 这里需要一个方法来绑定数据和选中状态
    /*fun bind(item: Any, isSelected: Boolean, onItemSelected: (Int, Boolean) -> Unit) {
        // 绑定数据到其他视图...（如果有的话）
        val fileName = item as String
        fileNameTextView.text = fileName.toString().substringBeforeLast(".")
        // 设置选中状态
        checkBox.isChecked = isSelected
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            onItemSelected(adapterPosition, isChecked)
        }
    }*/

    fun bind(dataObject: JsonFileItem) {
        fileNameTextView.text = dataObject.fileName.toString().substringBeforeLast(".")
        checkBox.isChecked = dataObject.isChecked
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            // 更新数据模型中的选中状态
            dataObject.isChecked = isChecked

            // 可能需要在此处执行其他操作，比如保存用户的选择
        }
    }
}
