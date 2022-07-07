package com.unlone.app.data.write

import kotlinx.serialization.Serializable

@Serializable
data class StoryRequest(
    val jwt: String,
    val title: String,
    val content: String,
    val topic: String,
    val isPublished: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
)