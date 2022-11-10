package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.StoryItem

class FetchStoriesByTopicUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(
        topic: String,
        lastItemId: Int? = null
    ): StoryResult<List<SimpleStory>> {
        // requestedStory is null as it is not out use case here
        return storyRepository.fetchStoriesByTopic(topic, null, pagingItems, lastItemId)
    }

    companion object {
        private const val pagingItems = 10
    }
}