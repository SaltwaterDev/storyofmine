package com.example.app.domain.stories

import com.example.app.data.story.StoryRepository
import com.example.app.data.story.StoryResult
import com.example.app.domain.MockStoryRepository
import com.example.app.domain.entities.StoryItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class GetTopicStoriesForRequestedStoryUTest: FunSpec ({
    val storyRepository: StoryRepository = MockStoryRepository()

    test("test success"){
        val useCase = GetTopicStoriesForRequestedStoryUseCase(storyRepository)
        useCase("topic").shouldBeTypeOf<StoryResult.Success<StoryItem.StoriesByTopic>>()
    }
})