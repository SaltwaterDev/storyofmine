package com.unlone.app.domain.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.MockStoryRepository
import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.useCases.stories.FetchStoryDetailUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf


class FetchStoryDetailTest: FunSpec ({
    val storyRepository: StoryRepository = MockStoryRepository()

    test("test"){
        val useCase = FetchStoryDetailUseCase(storyRepository)
        useCase("storyId").shouldBeTypeOf<StoryResult.Success<Story>>()
    }
})