package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTopicStoriesForRequestedStoryUseCase(private val storyRepository: StoryRepository) {
    suspend operator fun invoke(
        requestStory: String,
        storiesPerTopic: Int = 4,
    ): StoryResult<StoryItem.StoriesByTopic> {

        return when (val result =
            storyRepository.getSameTopicStoriesWithTarget(requestStory, storiesPerTopic)) {

            is StoryResult.Success -> {
                val topicStory = result.data?.first()
                StoryResult.Success(
                    topicStory?.topic?.let { StoryItem.StoriesByTopic(it, topicStory.stories) })
            }
            is StoryResult.Failed -> StoryResult.Failed(result.errorMsg)
            is StoryResult.UnknownError -> StoryResult.UnknownError(result.errorMsg)
        }
    }

//    companion object {
//        private const val pagingItems = 10
//    }
}