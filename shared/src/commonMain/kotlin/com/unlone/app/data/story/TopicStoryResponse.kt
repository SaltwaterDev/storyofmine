package com.unlone.app.data.story

import kotlinx.serialization.Serializable

@Serializable
data class TopicStoryResponse(
    val topic: String,
    val stories: List<SimpleStory>
)

@Serializable
data class StoriesPerTopicsResponse(
    val data: List<TopicStoryResponse>
)