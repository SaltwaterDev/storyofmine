package com.unlone.app.utils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.unlone.app.instance.Post

class PostDiffUtil(
        private val oldList: List<Post>,
        private val newList: List<Post>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.d("TAG", "areItemsTheSame: oldItemPosition: $oldItemPosition; newItemPosition: $newItemPosition")
        return oldList[oldItemPosition].pid == newList[newItemPosition].pid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.d("TAG", "areContentsTheSame: oldItemPosition: $oldItemPosition; newItemPosition: $newItemPosition")
        return when {
            oldList[oldItemPosition].pid != newList[newItemPosition].pid -> {
                false
            }
            oldList[oldItemPosition].title != newList[newItemPosition].title -> {
                false
            }
            oldList[oldItemPosition].imagePath != newList[newItemPosition].imagePath -> {
                false
            }
            oldList[oldItemPosition].journal != newList[newItemPosition].journal -> {
                false
            }
            oldList[oldItemPosition].author_uid != newList[newItemPosition].author_uid -> {
                false
            }
            oldList[oldItemPosition].labels != newList[newItemPosition].labels -> {
                false
            }
            oldList[oldItemPosition].createdTimestamp != newList[newItemPosition].createdTimestamp -> {
                false
            }
            oldList[oldItemPosition].pid != newList[newItemPosition].pid -> {
                false
            }
            oldList[oldItemPosition].comment != newList[newItemPosition].comment -> {
                false
            }
            oldList[oldItemPosition].save != newList[newItemPosition].save -> {
                false
            }
            else -> true
        }
    }
}