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
import com.unlone.app.databinding.ListItemSubCommentBinding
import com.unlone.app.model.*


class SubCommentsAdapter(
    val viewModel: DetailedPostViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<UiSubComment, SubCommentsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(
        val binding: ListItemSubCommentBinding,
        val viewModel: DetailedPostViewModel,
        private val lifecycleOwner: LifecycleOwner
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiSubComment) {
            Log.d("TAG", "viewModel: $viewModel")
            binding.uiSubComment = item
            binding.lifecycleOwner = lifecycleOwner
            binding.likeButton.setOnClickListener {
                if (it.tag == "liked") {
                    it.tag = "like"
                    (it as ImageView).setImageResource(R.drawable.ic_heart)
                } else {
                    it.tag = "liked"
                    (it as ImageView).setImageResource(R.drawable.ic_heart_filled)
                }
                viewModel.processSubCommentLike(item.subComment)
            }
            binding.moreButton.setOnClickListener { v: View ->
                showMenu(v, R.menu.comment_popup_menu, item.subComment, viewModel)
            }
            binding.commentButton.setOnClickListener {
                // focus text
                item.subComment.cid?.let { cid ->
                    item.subComment.username?.let { username ->
                        viewModel.focusEdittextToSubComment(
                            cid,
                            username
                        )
                    }
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewModel: DetailedPostViewModel,
                lifecycleOwner: LifecycleOwner
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSubCommentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, viewModel, lifecycleOwner)
            }
        }

        private fun showMenu(
            v: View,
            @MenuRes menuRes: Int,
            subComment: SubComment,
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
                                    viewModel.reportSubComment(subComment, checkedItem)
                                    showConfirmation(v.parent as MaterialCardView)

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

    object DiffCallback : DiffUtil.ItemCallback<UiSubComment>() {
        override fun areItemsTheSame(
            oldItem: UiSubComment,
            newItem: UiSubComment
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: UiSubComment,
            newItem: UiSubComment
        ): Boolean {
            return when {
                oldItem.subComment.cid != newItem.subComment.cid -> {
                    false
                }
                oldItem.subComment.uid != newItem.subComment.uid -> {
                    false
                }
                oldItem.subComment.content != newItem.subComment.content -> {
                    false
                }
                oldItem.subComment.username != newItem.subComment.username -> {
                    false
                }
                oldItem.subComment.score != newItem.subComment.score -> {
                    false
                }
                oldItem.subComment.timestamp != newItem.subComment.timestamp -> {
                    false
                }
                oldItem.subComment.parent_cid != newItem.subComment.parent_cid -> {
                    false
                }
                else -> true
            }
        }
    }
}
