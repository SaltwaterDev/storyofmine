package com.unlone.app.domain.entities

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val topics: String,
    val isPublished: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
    val comment: Comment?
)

