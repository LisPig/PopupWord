package com.example.wordunlock.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.models.FavoriteItem
import com.example.wordunlock.databinding.CollectionListBinding

class FavoriteItemViewHolder(private val binding: CollectionListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(favoriteItem: FavoriteItem, onDeleteClickListener: (FavoriteItem) -> Unit) {
        binding.tvWord.text = favoriteItem.word
        binding.ivDelete.setOnClickListener {
            onDeleteClickListener.invoke(favoriteItem)
        }
    }
}