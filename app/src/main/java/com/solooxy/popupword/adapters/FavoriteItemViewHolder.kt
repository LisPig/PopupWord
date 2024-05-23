package com.solooxy.popupword.adapters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.solooxy.popupword.models.FavoriteItem


import com.solooxy.popupword.R
import com.solooxy.popupword.databinding.CollectionListBinding


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