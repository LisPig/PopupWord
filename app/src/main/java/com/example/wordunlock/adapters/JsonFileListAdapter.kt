package com.example.wordunlock.adapters

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R


class JsonFileListAdapter(private val context: Context,  private val fileNames: Array<String>?) :
    RecyclerView.Adapter<JsonFileListAdapter.FileViewHolder>() {

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_raw_file, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileName = fileNames?.get(position)
        //var isChecked: Boolean = context.get(position)
        holder.fileNameTextView.text = fileName.toString().substringBeforeLast(".")

    }

    override fun getItemCount(): Int {
        return fileNames!!.size
    }
}