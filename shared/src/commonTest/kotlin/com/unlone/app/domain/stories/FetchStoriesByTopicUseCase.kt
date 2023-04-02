package com.unlone.app.domain.stories

import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.MockStoryRepository
import com.unlone.app.domain.useCases.stories.FetchStoriesByTopicUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class FetchStoriesByTopicTest: FunSpec({

    val repo = MockStoryRepository()
    test("test success"){
        val useCase = FetchStoriesByTopicUseCase(repo)
        useCase("topic").shouldBeTypeOf<StoryResult.Success<List<SimpleStory>>>()
    }
})