package com.example.app.domain.stories

import com.example.app.data.story.StoryRepository
import com.example.app.data.story.StoryResult
import com.example.app.domain.MockStoryRepository
import com.example.app.domain.entities.Story
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf


class FetchStoryDetailTest: FunSpec ({
    val storyRepository: StoryRepository = MockStoryRepository()

    test("test"){
        val useCase = FetchStoryDetailUseCase(storyRepository)
        useCase("storyId").shouldBeTypeOf<StoryResult.Success<Story>>()
    }
})