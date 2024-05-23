package com.solooxy.popupword.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.solooxy.popupword.R
import com.solooxy.popupword.models.WordList

class WordListAdapter(
    private val context: Context,
    private val wordLists: MutableList<WordList>,
    private val onWordListSelected: (WordList, Boolean) -> Unit
) : RecyclerView.Adapter<WordListAdapter.WordListViewHolder>() {

    // ViewHolder class implementation...
    inner class WordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(wordList: WordList, onCheckboxClicked: (WordList, Boolean) -> Unit) {
            titleTextView.text = wordList.name

            checkbox.setOnCheckedChangeListener(null) // 防止重复触发
            checkbox.isChecked = wordList.isSelected
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxClicked(wordList, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_word_list, parent, false)
        return WordListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val wordList = wordLists[position]
        holder.bind(wordList, onWordListSelected)
    }

    override fun getItemCount(): Int = wordLists.size
}