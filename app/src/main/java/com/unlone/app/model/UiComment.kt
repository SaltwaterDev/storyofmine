package com.unlone.app.model

import androidx.annotation.Keep

@Keep
data class UiComment(
    val comment: Comment,
    var likedByUser: Boolean,
    var commentExpanded: Boolean,
    var uiSubComments: List<UiSubComment>?
)
