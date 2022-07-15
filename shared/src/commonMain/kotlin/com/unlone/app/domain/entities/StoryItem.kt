package com.unlone.app.domain.entities

import com.unlone.app.data.story.SimpleStory

sealed class StoryItem {
    data class StoriesByTopic(
        val topic: String,
        val stories: List<SimpleStory>
    )
    data class TopicTable(
        val topics: List<String>
    )
}