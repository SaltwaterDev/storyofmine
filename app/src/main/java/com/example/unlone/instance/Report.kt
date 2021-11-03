package com.example.unlone.instance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class Report(){
    data class PostReport(
        val type: String = "post",
        val post: Post? = null,
        val reportReason: String? = null,
        val reportedBy: String): Report()

    data class CommentReport(
        val type: String = "comment",
        val comment: Comment? = null,
        val reportReason: String? = null,
        val reportedBy: String): Report()

}