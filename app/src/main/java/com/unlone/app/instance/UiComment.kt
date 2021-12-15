package com.unlone.app.instance

import androidx.annotation.Keep

@Keep
data class UiComment(
    val comment: Comment,
    var likedByUser: Boolean,
    var uiSubComments: List<UiSubComment>?
)
