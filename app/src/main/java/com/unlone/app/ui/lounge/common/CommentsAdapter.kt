package com.unlone.app.ui.lounge.common

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.google.android.material.card.MaterialCardView
import com.unlone.app.databinding.RecyclerviewCommentBinding
import com.unlone.app.instance.Comment
import com.unlone.app.instance.Report
import com.unlone.app.utils.convertTimeStamp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

import com.unlone.app.R
import com.unlone.app.instance.Post
import com.unlone.app.instance.SubComment
import com.unlone.app.utils.CommentDiffUtil
import com.unlone.app.utils.PostDiffUtil


open class CommentsAdapter(
    private val pid: String,
    private val onLikeCallback: (Comment) -> Unit,
    private val onSubCommentLikeCallback: (SubComment) -> Unit,
    private val onFocusEdittextCallback: (String, String) -> Unit
) :
    ListAdapter<Comment, CommentsAdapter.ViewHolder>(CommentDiffCallback) {

    private var commentList = emptyList<Comment>()
    var selfPost: Boolean = false
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var context: Context? = null

    private val viewPool = RecycledViewPool()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var username: TextView = itemView.findViewById(R.id.username)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var commentTv: TextView = itemView.findViewById(R.id.comment)
        private var likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        private var moreButton: ImageView = itemView.findViewById(R.id.moreButton)
        private var commentButton: ImageView = itemView.findViewById(R.id.commentButton)
        private var cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private var subCommentRv: RecyclerView = itemView.findViewById(R.id.subCommentRv)

        fun bind(comment: Comment){
            // set comment content
            username.text = comment.username
            date.text = comment.timestamp?.let { convertTimeStamp(it, "COMMENT") }
            commentTv.text = comment.content

            // init "like" button
            likeButton.setOnClickListener {
                onLikeCallback(comment)
            }
            isLiked(likeButton, comment)

            // init "more" button
            moreButton.setOnClickListener { v: View ->
                showMenu(v, R.menu.comment_popup_menu, comment, cardView)
            }

            // set sub comments recycler view
            val subCommentAdapter = SubCommentsAdapter(
                subCommentRv.context,
                pid,
                onSubCommentLikeCallback,
                onFocusEdittextCallback
            )
            subCommentRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = subCommentAdapter
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
            }
            comment.subComments?.let {
                subCommentAdapter.setSubCommentList(it)
            }


            // init "comment" button
            if (comment.subComments?.size ?: 0 > 0) {
                commentButton.setImageResource(R.drawable.ic_chat_filled)
                commentButton.tag = "to read"
            } else {
                commentButton.tag = "to write"
            }
            commentButton.setOnClickListener {
                val repliedName = comment.username
                val repliedCid = comment.cid
                // if the comment icon is outlined
                if (commentButton.tag == "to write") {
                    if (repliedName != null && repliedCid != null) {
                        onFocusEdittextCallback(repliedCid, repliedName)
                    }
                } else if (commentButton.tag == "to read") {
                    Log.d("TAG", "subComments recyclerview: ${comment.subComments}")
                    subCommentRv.visibility = View.VISIBLE
                    commentButton.setImageResource(R.drawable.ic_chat)
                    commentButton.tag = "to write"
                } else {
                    Toast.makeText(
                        context, "Unexpected error: no tag for commentButton ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_comment, parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /*
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = commentList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
     */

    /*
    fun setCommentList(newCommentList: List<Comment>) {
        Log.d("TAG", "setCommentList oldCommentList: ${this.commentList}")
        Log.d("TAG", "setCommentList newCommentList: $newCommentList")
        val diffUtil = CommentDiffUtil(this.commentList, newCommentList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        this.commentList = newCommentList
        diffResults.dispatchUpdatesTo(this)
    }
     */

    private fun isLiked(likeButton: ImageView, comment: Comment) {
        mFirestore.collection("posts").document(pid)
            .collection("comments").document(comment.cid!!)
            .collection("likes").whereEqualTo("likedBy", mAuth.uid)
            .addSnapshotListener { snapshot, e ->
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
                assert(likeList.size <= 1)
                Log.d(TAG, "People who has liked: $likeList")

                if (likeList.size == 1) {    // user has liked
                    likeButton.setImageResource(R.drawable.ic_heart_filled)
                    likeButton.tag = "liked"
                } else {
                    likeButton.setImageResource(R.drawable.ic_heart)
                    likeButton.tag = "like"
                }
            }
    }

    private fun showMenu(
        v: View,
        @MenuRes menuRes: Int,
        comment: Comment,
        commentView: MaterialCardView
    ) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            when (menuItem.itemId) {
                R.id.action_comment_report -> {
                    context?.let { it ->
                        val reportMap = mapOf(
                            it.getString(R.string.hate_speech) to "Hate Speech",
                            it.getString(R.string.span_or_irrelevant) to "Span or Irrelevant",
                            it.getString(R.string.sexual_or_inappropriate) to "Sexual or Inappropriate",
                            it.getString(R.string.just_dont_like) to "I just don’t like it"
                        )
                        val singleItems = reportMap.keys.toList().toTypedArray()
                        var checkedItem = 1

                        // show dialog
                        MaterialAlertDialogBuilder(it, R.style.ThemeOverlay_App_MaterialAlertDialog)
                            .setTitle(it.getString(R.string.why_report))
                            .setNeutralButton(it.getString(R.string.cancel)) { _, _ ->
                                // Respond to neutral button press
                            }
                            .setPositiveButton(it.getString(R.string.report)) { _, _ ->
                                // Respond to positive button press
                                Log.d("TAG", singleItems[checkedItem])
                                val report = mAuth.uid?.let { it1 ->
                                    Report.CommentReport(
                                        comment = comment,
                                        reportReason = reportMap[singleItems[checkedItem]],
                                        reportedBy = it1
                                    )
                                }

                                Log.d("TAG", report.toString())
                                if (report != null) {
                                    mFirestore.collection("reports")
                                        .add(report)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "TAG",
                                                "Report DocumentSnapshot successfully written!"
                                            )
                                            showConfirmation(commentView)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "TAG",
                                                "Error saving post\n",
                                                e
                                            )
                                        }
                                }

                            }// Single-choice items (initialized with checked item)
                            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                                // Respond to item chosen
                                Log.d("TAG", which.toString())
                                checkedItem = which

                            }
                            .show()
                    }

                    true
                }
                else -> false
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun showConfirmation(commentView: MaterialCardView) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(it.getString(R.string.thank_you))
                .setMessage(it.getString(R.string.report_text))
                .setPositiveButton(it.getString(R.string.confirm)) { dialog, which ->
                    // Hide the comment
                    commentView.visibility = View.GONE
                }
                .show()
        }
    }

}

object CommentDiffCallback: DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return when {
            oldItem.cid != newItem.cid -> {
                false
            }
            oldItem.uid != newItem.uid-> {
                false
            }
            oldItem.content != newItem.content -> {
                false
            }
            oldItem.username != newItem.username -> {
                false
            }
            oldItem.score != newItem.score -> {
                false
            }
            oldItem.timestamp != newItem.timestamp -> {
                false
            }
            else -> true
        }
    }
}
