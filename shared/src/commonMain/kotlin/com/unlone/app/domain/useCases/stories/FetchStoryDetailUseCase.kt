package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.entities.StoryItem

class FetchStoryDetailUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(id: String): Story {
        return storyRepository.fetchStoryDetail(id)
    }
}