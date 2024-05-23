package com.solooxy.popupword.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.solooxy.popupword.databinding.CollectionListBinding
import com.solooxy.popupword.models.FavoriteItem

class FavoriteListAdapter(private val onDeleteClickListener: (FavoriteItem) -> Unit) :
    ListAdapter<FavoriteItem, FavoriteItemViewHolder>(FavoriteItemDiffCallback()) {

    var selectedPosition = -1
    val selectedItemOrNull: FavoriteItem?
        get() = if (selectedPosition >= 0 && selectedPosition < itemCount) getItem(selectedPosition) else null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        val binding = CollectionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onDeleteClickListener)
    }

    private class FavoriteItemDiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
        override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
            return oldItem == newItem
        }
    }



}