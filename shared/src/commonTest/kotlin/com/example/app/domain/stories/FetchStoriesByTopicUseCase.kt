package com.example.app.domain.stories

import com.example.app.data.story.SimpleStory
import com.example.app.data.story.StoryResult
import com.example.app.domain.MockStoryRepository
import com.example.app.domain.useCases.stories.FetchStoriesByTopicUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class FetchStoriesByTopicTest: FunSpec({

    val repo = MockStoryRepository()
    test("test success"){
        val useCase = FetchStoriesByTopicUseCase(repo)
        useCase("topic").shouldBeTypeOf<StoryResult.Success<List<SimpleStory>>>()
    }
})