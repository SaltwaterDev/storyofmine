package com.example.unlone.ui.Lounge

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.unlone.databinding.RecyclerviewCommentBinding
import com.example.unlone.instance.Comment
import com.example.unlone.instance.Post
import com.example.unlone.utils.CommentDiffUtil
import com.example.unlone.utils.convertTimeStamp

class CommentsAdapter :
        RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var commentList =  emptyList<Comment>()

    class ViewHolder(val binding: RecyclerviewCommentBinding)
        :RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "commentListt:　$commentList")
        holder.binding.username.text = commentList[position].author_username
        holder.binding.date.text = commentList[position].timestamp?.let { convertTimeStamp(it) }
        holder.binding.comment.text = commentList[position].content
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = commentList.size

    fun setCommentList(newCommentList: List<Comment>){
        val diffUtil = CommentDiffUtil(commentList, newCommentList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        commentList = newCommentList
        diffResults.dispatchUpdatesTo(this)
    }

    // TODO transfer timestamp to proper format


}
