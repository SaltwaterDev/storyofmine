package com.unlone.app.domain.entities

import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.Topic

sealed interface StoryItem {
    class StoriesByTopic(
        val topic: String = "Topic", val stories: List<SimpleStory> = List(3) { SimpleStory.mock() }

    ) : StoryItem

    class TopicTable(
        val topics: List<Topic>
    ) : StoryItem

    class UnknownError(errorMsg: String?) : StoryItem
}