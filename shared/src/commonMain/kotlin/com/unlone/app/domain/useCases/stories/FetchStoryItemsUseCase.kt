package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.domain.entities.StoryItem

class FetchStoryItemsUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(lastItemId: String? = null): List<StoryItem.StoriesByTopic> {
        return storyRepository.fetchStoriesByPosts(postsPerPage, pagingItems, lastItemId)
    }

    companion object {
        private const val postsPerPage = 10
        private const val pagingItems = 5
    }
}