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
        return storyRepository.fetchStoriesByTopic(topic, pagingItems, lastItemId)
    }

    companion object {
        private const val pagingItems = 10
    }
}