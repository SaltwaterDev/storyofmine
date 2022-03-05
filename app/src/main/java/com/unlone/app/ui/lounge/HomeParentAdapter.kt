package com.unlone.app.ui.lounge

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.CategoryRowPostsListItemBinding
import com.unlone.app.model.CtgPostsViewHolder
import com.unlone.app.model.HomeTipsViewHolder
import com.unlone.app.model.HomeUiModel
import com.unlone.app.utils.dpConvertPx


class HomeParentAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onClick: (String) -> Unit,
    private val onMorePostsClick: (String) -> Unit,
) :
    ListAdapter<HomeUiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.category_row_posts_list_item) {
            CtgPostsViewHolder.from(parent, lifecycleOwner, onClick, onMorePostsClick)
        } else {
            HomeTipsViewHolder.from(parent, lifecycleOwner)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel  = getItem(position)
        Log.d("TAG", "item loaded: $uiModel ")
        uiModel.let {
            when (uiModel) {
                is HomeUiModel.CtgPostItemUiState -> (holder as CtgPostsViewHolder).bind(uiModel)
                is HomeUiModel.Tips -> (holder as HomeTipsViewHolder).bind(uiModel)
            }
        }
    }


    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<HomeUiModel>() {
            override fun areItemsTheSame(oldItem: HomeUiModel, newItem: HomeUiModel): Boolean {
                return (oldItem is HomeUiModel.CtgPostItemUiState && newItem is HomeUiModel.CtgPostItemUiState &&
                        oldItem.category == newItem.category) ||
                        (oldItem is HomeUiModel.Tips && newItem is HomeUiModel.Tips &&
                                oldItem.title == newItem.title)
            }

            override fun areContentsTheSame(oldItem: HomeUiModel, newItem: HomeUiModel): Boolean =
                oldItem == newItem
        }
    }
}
