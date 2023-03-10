package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult


class PostStoryUseCase(
    private val storyRepository: StoryRepository,
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<String> {
        // check if title and content are not empty
        return if (title.isEmpty() || content.isEmpty()) {
            StoryResult.Failed(exception = PublishStoryException.EmptyTitleOrBodyException())
        } else if (topic.isEmpty()) {
            StoryResult.Failed(exception = PublishStoryException.EmptyTopicException())
        } else {
            val result = storyRepository.postStory(
                title,
                content,
                topic,
                isPublished,
                commentAllowed,
                saveAllowed
            )
            if (result is StoryResult.Success){
                storyRepository.setPrioritiseTopicStoriesRepresentative(storyId = result.data!!)
            }
            return result
        }
    }
}


