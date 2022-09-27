package com.unlone.app.model

import androidx.annotation.Keep
import com.unlone.app.domain.entities.Comment

@Keep
sealed class Report {
    /*data class StoryReport(
        val type: String = "post",
        val post: PostDetail? = null,
        val reportReason: String? = null,
        val reportedBy: String
    ) : Report()*/

    data class CommentReport(
        val type: String = "comment",
        val comment: Comment? = null,
        val reportReason: String? = null,
        val reportedBy: String
    ) : Report()

}