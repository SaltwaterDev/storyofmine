package com.example.app.domain.write

import com.example.app.data.story.PublishStoryException
import com.example.app.data.story.StoryResult
import com.example.app.domain.MockStoryRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf


class PostStoryTest : FunSpec({
    val storyRepository = MockStoryRepository()

    test("test post story success") {
        val useCase = PostStoryUseCase(storyRepository)
        val result = useCase("title", "content", "topic", true, true, true)
        result.shouldBeTypeOf<StoryResult.Success<String>>()
    }

    test("test post story empty title fail") {
        val useCase = PostStoryUseCase(storyRepository)
        val result = useCase("title", "", "topic", true, true, true)
        result.shouldBeTypeOf<StoryResult.Failed<String>>()
        result.exception.shouldBe(PublishStoryException.EmptyTitleOrBodyException())
    }
    test("test post story empty topic fail") {
        val useCase = PostStoryUseCase(storyRepository)
        val result = useCase("title", "content", "", true, true, true)
        result.shouldBeTypeOf<StoryResult.Failed<String>>()
        result.exception.shouldBe(PublishStoryException.EmptyTopicException())
    }
})