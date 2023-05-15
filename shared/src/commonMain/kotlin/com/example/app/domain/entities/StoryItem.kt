package com.example.app.domain.entities

import com.example.app.data.story.SimpleStory
import com.example.app.data.story.Topic

sealed interface StoryItem {
    class StoriesByTopic(
        val topic: String = "Topic", val stories: List<SimpleStory> = List(3) { SimpleStory.mock() }

    ) : StoryItem

    class TopicTable(
        val topics: List<Topic>
    ) : StoryItem

    class UnknownError(errorMsg: String?) : StoryItem
}