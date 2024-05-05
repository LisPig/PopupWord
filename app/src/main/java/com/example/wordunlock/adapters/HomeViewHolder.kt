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


    fun bind(dataObject: JsonFileItem,onItemSelected: ((Int, Boolean) -> Unit)?) {
        fileNameTextView.text = dataObject.fileName.toString().substringBeforeLast(".")
        checkBox.isChecked = dataObject.isChecked
        itemView.findViewById<CheckBox>(R.id.checkBox).apply {
            isChecked = dataObject.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                dataObject.isChecked = isChecked
                // 通知适配器数据已更改
                onItemSelected?.invoke(adapterPosition, isChecked) // 通知Adapter选中状态改变
            }
        }
    }

}
