package com.example.unlone.ui.lounge.common

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.unlone.R
import com.example.unlone.databinding.RecyclerviewCommentBinding
import com.example.unlone.instance.Comment
import com.example.unlone.utils.convertTimeStamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList


class CommentsAdapter(private val pid: String, private val onLikeCallback: (Comment) -> Unit) :
        RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var commentList =  emptyList<Comment>()
    private lateinit var recyclerView: RecyclerView
    var selfPost: Boolean = false
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userLikeId: String? = null

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
        Log.d("TAG", "commentList:　$commentList")
        holder.binding.username.text = commentList[position].username
        holder.binding.date.text = commentList[position].timestamp?.let { convertTimeStamp(it, "COMMENT") }
        holder.binding.comment.text = commentList[position].content

        holder.binding.likeButton.setOnClickListener {
            onLikeCallback(commentList[position])
        }
        isLiked(holder.binding.likeButton, commentList[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = commentList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun setCommentList(newCommentList: List<Comment>){
        commentList = newCommentList
    }


    private fun isLiked(likeButton: ImageView, comment: Comment) {
        mFirestore.collection("posts").document(pid)
            .collection("comments").document(comment.cid!!)
            .collection("likes").whereEqualTo("likedBy", mAuth.uid)
            .addSnapshotListener{snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }
                val likeList = ArrayList<String>()
                for (doc in snapshot!!) {
                    doc.getString("likedBy")?.let {
                        likeList.add(it)
                    }
                }
                assert (likeList.size <= 1)
                Log.d(TAG, "People who has liked: $likeList")

                if (likeList.size == 1){    // user has liked
                    likeButton.setImageResource(R.drawable.ic_heart_filled)
                    likeButton.tag = "liked"
                } else{
                    likeButton.setImageResource(R.drawable.ic_heart)
                    likeButton.tag = "like"
                }
            }
    }
}
