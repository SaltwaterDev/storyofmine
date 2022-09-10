package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.domain.entities.StoryItem

class FetchStoryItemsUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(): List<StoryItem.StoriesByTopic> {
        return storyRepository.fetchStoriesByPosts(postsPerPage, itemsPerPage)
    }

    companion object {
        private const val postsPerPage = 7
        private const val itemsPerPage = 5
    }
}