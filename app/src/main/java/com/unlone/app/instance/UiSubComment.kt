package com.unlone.app.instance

import androidx.annotation.Keep

@Keep
data class UiSubComment(
    val subComment: SubComment,
    val likedByUser: Boolean
)
