package com.unlone.app.ui.lounge.category

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class AllCateListAdapter(private val onTopicClick: (String) -> Unit) :
    ListAdapter<String, FollowingCateListAdapter.ViewHolder>(CategoryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingCateListAdapter.ViewHolder {
        Log.d("TAG", "AllCateListAdapter view holder called")
        return FollowingCateListAdapter.ViewHolder.from(parent, onTopicClick)
    }


    override fun onBindViewHolder(holder: FollowingCateListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }
}
