package com.example.wordunlock.adapters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R
import com.example.wordunlock.models.FavoriteItem
import com.example.wordunlock.databinding.CollectionListBinding

class FavoriteItemViewHolder(private val binding: CollectionListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete)

    fun bind(favoriteItem: FavoriteItem, onDeleteClickListener: (FavoriteItem) -> Unit) {
        binding.tvWord.text = favoriteItem.word
        binding.ivDelete.setOnClickListener {
            onDeleteClickListener(favoriteItem)
        }
    }
}