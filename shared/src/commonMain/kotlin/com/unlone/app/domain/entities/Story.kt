package com.unlone.app.domain.entities

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val topic: String,
    val author: String,
    val isPublished: Boolean,
    val isSelfWritten: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
    val comment: Comment?,
    val createdDate: String,
    val isSaved: Boolean,
)

