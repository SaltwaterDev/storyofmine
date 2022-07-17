package com.unlone.app.data.story

import kotlinx.serialization.Serializable

@Serializable
data class StoryRequest(
    val title: String,
    val content: String,
    val topic: String,
    val isPublished: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
)


@Serializable
data class SimpleStory(
    val id: String,
    val title: String,
    val content: String,
    val topic: String,
)