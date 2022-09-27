package com.unlone.app.data.story

import kotlinx.serialization.Serializable


@kotlinx.serialization.Serializable
data class CommentResponse(
    val data: List<SingleCommentResponse>
){
    @Serializable
    data class SingleCommentResponse(
        val id: String,
        val story: String,
        val text: String,
        val author: String,
        val createdTime: String,
        val isWriter: Boolean,
    )
}



@Serializable
data class CommentRequest(
    val story: String,
    val text: String,
)