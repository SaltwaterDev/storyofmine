package com.unlone.app.model

import androidx.annotation.Keep
import com.unlone.app.domain.entities.Comment

@Keep
data class CommentUiState(
    val comment: Comment,
    var likedByUser: Boolean,
    var commentExpanded: Boolean,
    var subCommentUiStates: List<SubCommentUiState>?
)
