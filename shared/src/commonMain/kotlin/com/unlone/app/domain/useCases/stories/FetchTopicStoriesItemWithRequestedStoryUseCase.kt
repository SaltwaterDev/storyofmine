package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.StoryItem

class FetchTopicStoriesItemWithRequestedStoryUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(
        requestStory: String,
        lastItemId: Int? = null
    ): StoryResult<StoryItem.StoriesByTopic> {

        return when (val result =
            storyRepository.fetchStoriesByTopic(
                null,
                requestStory,
                pagingItems,
                lastItemId
            )) {
            is StoryResult.Success -> StoryResult.Success(
                result.data?.first()?.topic?.let {
                    StoryItem.StoriesByTopic(
                        it,
                        result.data,
                    )
                })
            is StoryResult.Failed -> StoryResult.Failed(result.errorMsg)
            is StoryResult.UnknownError -> StoryResult.UnknownError(result.errorMsg)
        }
    }

    companion object {
        private const val pagingItems = 10
    }
}