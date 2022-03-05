package com.unlone.app.model

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.CategoryRowPostsListItemBinding
import com.unlone.app.ui.lounge.ChildPostsAdapter
import com.unlone.app.ui.lounge.HomeParentAdapter
import com.unlone.app.utils.dpConvertPx

class CtgPostsViewHolder private constructor(
    val binding: CategoryRowPostsListItemBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val onClick: (String) -> Unit,
    private val onMorePostsClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HomeUiModel.CtgPostItemUiState) {
        binding.parentPostItemUiState = item
        val adapter = ChildPostsAdapter(onClick)
        binding.childPosts.adapter = adapter
        val mDividerItemDecoration = SpaceDividerItemDecoration.from(
            -7,
            item.postsUiStateItemList.size,
            binding.childPosts.context,
            DividerItemDecoration.HORIZONTAL
        )
        ContextCompat.getDrawable(
            binding.childPosts.context,
            R.drawable.card_post_divider
        )?.let { mDividerItemDecoration.setDrawable(it) }
        binding.childPosts.addItemDecoration(mDividerItemDecoration)
        binding.loadMoreTv.setOnClickListener {
            onMorePostsClick(item.category)
        }

        binding.lifecycleOwner = lifecycleOwner
        adapter.submitList(item.postsUiStateItemList)
        binding.executePendingBindings()
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            onClick: (String) -> Unit,
            onMorePostsClick: (String) -> Unit,
        ): CtgPostsViewHolder {
            Log.d("TAG", "view holder creating...")
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CategoryRowPostsListItemBinding.inflate(layoutInflater, parent, false)
            return CtgPostsViewHolder(binding, lifecycleOwner, onClick, onMorePostsClick)
        }
    }
}

class SpaceDividerItemDecoration(
    private val space: Int,
    private val itemCount: Int,
    val context: Context,
    orientation: Int
) :
    DividerItemDecoration(context, orientation) {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = 0
        outRect.left = -10
        val rightSpace = 25
        outRect.right = dpConvertPx(rightSpace, context)
        outRect.bottom = 0

        // Add left margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.right = 0

        }

    }

    companion object {
        fun from(
            space: Int,
            itemCount: Int,
            context: Context,
            orientation: Int
        ): SpaceDividerItemDecoration {
            return SpaceDividerItemDecoration(space, itemCount, context, orientation)

        }
    }
}

