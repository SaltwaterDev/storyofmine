package com.unlone.app.ui.lounge

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.ListItemCardCategoryBinding

class CategoriesAdapter(private val onTopicClick: (String) -> Unit) :
    ListAdapter<TopicCard, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {


    class ViewHolder private constructor(
        val binding: ListItemCardCategoryBinding,
        val onTopicClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TopicCard) {
            if (item.title == R.string.topic_more.toString())
                // special case: that item is for load more topic
                binding.topic = binding.root.resources.getString(item.title.toInt())
            else
                binding.topic = item.title
            val resources = binding.root.resources
            binding.cardView.setCardBackgroundColor(
                resources.getColor(item.color)
            )
            binding.executePendingBindings()
            if (item.title == "No Such Topic"){
                binding.cardView.isEnabled = false
            }
            binding.cardView.setOnClickListener { onTopicClick(item.title) }
        }

        companion object {
            fun from(parent: ViewGroup, onTopicClick: (String) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCardCategoryBinding.inflate(layoutInflater, parent, false)
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

class CategoryDiffCallback : DiffUtil.ItemCallback<TopicCard>() {

    override fun areItemsTheSame(oldItem: TopicCard, newItem: TopicCard): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: TopicCard, newItem: TopicCard): Boolean {
        return oldItem == newItem
    }
}