package com.unlone.app.ui.lounge

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.databinding.ListItemCardPostBinding
import com.unlone.app.model.PostItemUiState

class ChildPostsAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<PostItemUiState, ChildPostsAdapter.ViewHolder>(PostDiffCallback()) {

    class ViewHolder private constructor(
        val binding: ListItemCardPostBinding,
        private val onClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostItemUiState) {
            binding.postUiState = item
            binding.executePendingBindings()
            binding.cardView.setOnClickListener { onClick(item.pid) }
        }

        companion object {
            fun from(parent: ViewGroup, onClick: (String) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCardPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClick)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onClick)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }
}