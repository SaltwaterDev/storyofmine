package com.unlone.app.ui.lounge

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.databinding.ListItemPostBinding
import com.unlone.app.model.PostItemUiState

class PostListAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<PostItemUiState, PostListAdapter.ViewHolder>(PostDiffCallback()) {

    class ViewHolder private constructor(
        val binding: ListItemPostBinding,
        private val itemClickListener: ItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostItemUiState) {
            binding.postUiState = item
            binding.cardView.setOnClickListener {
                itemClickListener.onClick(item.pid)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, itemClickListener: ItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, itemClickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<PostItemUiState>() {

    override fun areItemsTheSame(oldItem: PostItemUiState, newItem: PostItemUiState): Boolean {
        return oldItem.pid == newItem.pid
    }


    override fun areContentsTheSame(oldItem: PostItemUiState, newItem: PostItemUiState): Boolean {
        return oldItem == newItem
    }
}