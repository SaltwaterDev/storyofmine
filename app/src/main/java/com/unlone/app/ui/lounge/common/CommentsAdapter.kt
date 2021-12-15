package com.unlone.app.ui.lounge.common

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unlone.app.R
import com.unlone.app.databinding.ListItemCommentBinding
import com.unlone.app.instance.Comment
import com.unlone.app.instance.Report
import com.unlone.app.instance.UiComment


open class CommentsAdapter(
    val viewModel: DetailedPostViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<UiComment, CommentsAdapter.ViewHolder>(DiffCallback) {



    class ViewHolder private constructor(
        val binding: ListItemCommentBinding,
        val viewModel: DetailedPostViewModel,
        private val lifecycleOwner: LifecycleOwner
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val subCommentsAdapter by lazy {
            SubCommentsAdapter(
                viewModel,
                lifecycleOwner,
            )
        }
        fun bind(item: UiComment) {
            Log.d("TAG", "viewModel: $viewModel")
            binding.uiComment = item
            binding.lifecycleOwner = lifecycleOwner
            binding.likeButton.setOnClickListener {
                if (it.tag == "liked") {
                    it.tag = "like"
                    (it as ImageView).setImageResource(R.drawable.ic_heart)
                } else {
                    it.tag = "liked"
                    (it as ImageView).setImageResource(R.drawable.ic_heart_filled)
                }
                viewModel.processCommentLike(item)
            }
            binding.moreButton.setOnClickListener { v: View ->
                showMenu(v, R.menu.comment_popup_menu, item.comment, viewModel)
            }
            binding.commentButton.setOnClickListener {
                if (item.uiSubComments.isNullOrEmpty()){
                    // focus text
                    item.comment.cid?.let { cid ->
                        item.comment.username?.let { username ->
                            viewModel.focusEdittextToSubComment(
                                cid,
                                username
                            )
                        }
                    }
                }else{
                    // open sub comments
                    (it as ImageView).setImageResource(R.drawable.ic_chat)
                    binding.subCommentRv.visibility = View.VISIBLE
                }
            }
            binding.subCommentRv.adapter = subCommentsAdapter
            subCommentsAdapter.submitList(item.uiSubComments)
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewModel: DetailedPostViewModel,
                lifecycleOwner: LifecycleOwner
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCommentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, viewModel, lifecycleOwner)
            }
        }

        private fun showMenu(
            v: View,
            @MenuRes menuRes: Int,
            comment: Comment,
            viewModel: DetailedPostViewModel
        ) {
            val popup = PopupMenu(v.context!!, v)
            popup.menuInflater.inflate(menuRes, popup.menu)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                // Respond to menu item click.
                when (menuItem.itemId) {
                    R.id.action_comment_report -> {
                        v.context?.let { it ->
                            val reportMap = mapOf(
                                it.getString(R.string.hate_speech) to "Hate Speech",
                                it.getString(R.string.span_or_irrelevant) to "Span or Irrelevant",
                                it.getString(R.string.sexual_or_inappropriate) to "Sexual or Inappropriate",
                                it.getString(R.string.just_dont_like) to "I just don’t like it"
                            )
                            val singleItems = reportMap.keys.toList().toTypedArray()
                            var checkedItem = 1

                            // show dialog
                            MaterialAlertDialogBuilder(
                                it,
                                R.style.ThemeOverlay_App_MaterialAlertDialog
                            )
                                .setTitle(it.getString(R.string.why_report))
                                .setNeutralButton(it.getString(R.string.cancel)) { _, _ ->
                                    // Respond to neutral button press
                                }
                                .setPositiveButton(it.getString(R.string.report)) { _, _ ->
                                    // Respond to positive button press
                                    Log.d("TAG", singleItems[checkedItem])
                                    val report = comment.uid?.let { it1 ->
                                        Report.CommentReport(
                                            comment = comment,
                                            reportReason = reportMap[singleItems[checkedItem]],
                                            reportedBy = it1
                                        )
                                    }

                                    Log.d("TAG", report.toString())
                                    if (report != null) {
                                        viewModel.uploadReport(report)
                                        showConfirmation(v.parent as MaterialCardView)
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
            commentView.context?.let {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }

    object DiffCallback : DiffUtil.ItemCallback<UiComment>() {
        override fun areItemsTheSame(oldItem: UiComment, newItem: UiComment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UiComment, newItem: UiComment): Boolean {
            return when {
                oldItem.likedByUser != newItem.likedByUser -> {
                    false
                }
                oldItem.comment.cid != newItem.comment.cid -> {
                    false
                }
                oldItem.comment.uid != newItem.comment.uid -> {
                    false
                }
                oldItem.comment.content != newItem.comment.content -> {
                    false
                }
                oldItem.comment.username != newItem.comment.username -> {
                    false
                }
                oldItem.comment.score != newItem.comment.score -> {
                    false
                }
                oldItem.comment.timestamp != newItem.comment.timestamp -> {
                    false
                }
                else -> true
            }
        }
    }
}
