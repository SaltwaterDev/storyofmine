package com.example.app.data.repo

import com.example.app.data.repo.mockObjects.MockStoryApi
import com.example.app.data.story.StoryResult
import com.example.app.data.story.Topic
import com.example.app.data.story.TopicRepositoryImpl
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class TopicRepositoryTest : FunSpec({
    val storyApi = MockStoryApi()

    test("get All Topic") {
        val topicRepository = TopicRepositoryImpl(storyApi)
        topicRepository.getAllTopic().shouldBeTypeOf<StoryResult.Success<List<Topic>>>()
    }

    test("get Random Topic") {
        val topicRepository = TopicRepositoryImpl(storyApi)
        topicRepository.getRandomTopic(4).shouldBeTypeOf<StoryResult.Success<List<Topic>>>()
    }
})