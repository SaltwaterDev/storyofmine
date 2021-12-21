package com.unlone.app.ui.lounge.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.model.Post
import com.unlone.app.databinding.ListItemPostBinding

class PostsAdapter :
    ListAdapter<Post, PostsAdapter.ViewHolder>(PostDiffCallback()) {

    class ViewHolder private constructor(val binding: ListItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            binding.post = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.pid == newItem.pid
    }


    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}