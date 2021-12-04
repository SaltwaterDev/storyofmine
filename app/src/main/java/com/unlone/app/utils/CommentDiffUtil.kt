package com.unlone.app.utils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.unlone.app.instance.Comment

class CommentDiffUtil(
        private val oldList: List<Comment>,
        private val newList: List<Comment>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.d("TAG", "areItemsTheSame: oldItemPosition: $oldItemPosition; newItemPosition: $newItemPosition")
        return oldList[oldItemPosition].cid == newList[newItemPosition].cid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.d("TAG", "areContentsTheSame: oldItemPosition: $oldItemPosition; newItemPosition: $newItemPosition")
        return when {
            oldList[oldItemPosition].cid != newList[newItemPosition].cid -> {
                false
            }
            oldList[oldItemPosition].uid != newList[newItemPosition].uid-> {
                false
            }
            oldList[oldItemPosition].content != newList[newItemPosition].content -> {
                false
            }
            oldList[oldItemPosition].username != newList[newItemPosition].username -> {
                false
            }
            oldList[oldItemPosition].score != newList[newItemPosition].score -> {
                false
            }
            oldList[oldItemPosition].timestamp != newList[newItemPosition].timestamp -> {
                false
            }
            else -> true
        }
    }
}