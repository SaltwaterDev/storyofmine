package com.example.unlone.utils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.unlone.instance.Comment

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
        Log.d("TAG", "areItemsTheSame: "+ oldList[oldItemPosition].hashCode() +" "+ newList[newItemPosition].hashCode())
        return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].hashCode() != newList[newItemPosition].hashCode() -> {
                false
            }
            oldList[oldItemPosition].author_uid != newList[newItemPosition].author_uid -> {
                false
            }
            oldList[oldItemPosition].author_username != newList[newItemPosition].author_username -> {
                false
            }
            oldList[oldItemPosition].content != newList[newItemPosition].content -> {
                false
            }
            oldList[oldItemPosition].timestamp != newList[newItemPosition].timestamp -> {
                false
            }
            else -> true
        }
    }
}