package com.unlone.app.domain.entities

import com.unlone.app.data.story.SimpleStory

sealed class StoryItem {
    data class StoriesByTopic(
        val topic: String = "Topic",
        val stories: List<SimpleStory> = listOf(
            SimpleStory(
                "_id",
                "_title",
                "_content",
                "_topic",
            )
        )
    )
    data class TopicTable(
        val topics: List<String>
    )
}