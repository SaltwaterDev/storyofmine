package com.unlone.app.ui.lounge.category

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.databinding.ListItemBinding

class FollowingCateListAdapter(private val onTopicClick: (String) -> Unit) :
    ListAdapter<String, FollowingCateListAdapter.ViewHolder>(CategoryDiffCallback()) {


    class ViewHolder private constructor(
        val binding: ListItemBinding,
        val onTopicClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.Tv.text = item
            Log.d("TAG", "bind: $item")
            if (item == "No Such Topic") {
                binding.Tv.isEnabled = false
            }
            binding.Tv.setOnClickListener { onTopicClick(item) }
        }

        companion object {
            fun from(parent: ViewGroup, onTopicClick: (String) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onTopicClick)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("TAG", "on created view holder called")
        return ViewHolder.from(parent, onTopicClick)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}