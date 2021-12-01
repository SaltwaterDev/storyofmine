package com.unlone.app.ui.lounge.common

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.unlone.app.R
import com.unlone.app.databinding.RecyclerviewCommentBinding
import com.unlone.app.instance.Comment
import com.unlone.app.instance.Report
import com.unlone.app.utils.convertTimeStamp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class CommentsAdapter(private val pid: String, private val onLikeCallback: (Comment) -> Unit) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var commentList = emptyList<Comment>()
    private lateinit var recyclerView: RecyclerView
    var selfPost: Boolean = false
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userLikeId: String? = null
    private var context: Context? = null

    class ViewHolder(val binding: RecyclerviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewCommentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "commentList:　$commentList")
        holder.binding.username.text = commentList[position].username
        holder.binding.date.text =
            commentList[position].timestamp?.let { convertTimeStamp(it, "COMMENT") }
        holder.binding.comment.text = commentList[position].content
        holder.binding.likeButton.setOnClickListener {
            onLikeCallback(commentList[position])
        }
        isLiked(holder.binding.likeButton, commentList[position])
        holder.binding.moreButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.comment_popup_menu, commentList[position], holder.binding.cardView)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = commentList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun setCommentList(newCommentList: List<Comment>) {
        commentList = newCommentList
    }

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

    private fun showMenu(v: View, @MenuRes menuRes: Int, comment: Comment, commentView: MaterialCardView) {
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
                                            Log.d("TAG", "Report DocumentSnapshot successfully written!")
                                            showConfirmation(commentView)
                                        }
                                        .addOnFailureListener { e -> Log.w("TAG", "Error saving post\n", e) }
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
        context?.let{
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
