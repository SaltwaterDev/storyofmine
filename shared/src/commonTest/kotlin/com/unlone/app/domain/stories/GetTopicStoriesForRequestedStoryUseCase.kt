package com.unlone.app.domain.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.MockStoryRepository
import com.unlone.app.domain.entities.StoryItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class GetTopicStoriesForRequestedStoryUTest: FunSpec ({
    val storyRepository: StoryRepository = MockStoryRepository()

    test("test success"){
        val useCase = GetTopicStoriesForRequestedStoryUseCase(storyRepository)
        useCase("topic").shouldBeTypeOf<StoryResult.Success<StoryItem.StoriesByTopic>>()
    }
})