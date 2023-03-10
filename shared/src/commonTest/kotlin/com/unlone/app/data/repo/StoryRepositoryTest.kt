package com.unlone.app.data.repo

import com.unlone.app.data.auth.AuthRepositoryImpl
import com.unlone.app.data.repo.mockObjects.MockAuthApi
import com.unlone.app.data.repo.mockObjects.MockKmmPreference
import com.unlone.app.data.repo.mockObjects.MockStoryApi
import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryRepositoryImpl
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.entities.StoryItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf

class StoryRepositoryTest : FunSpec({
    val authApi = MockAuthApi()
    val storyApi = MockStoryApi()
    val pref = MockKmmPreference()
    val authRepo = AuthRepositoryImpl(authApi, pref)

    test("fetchStoriesByPosts") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.fetchStoriesByPosts(0, 5, 5)
            .shouldBeInstanceOf<List<StoryItem.StoriesByTopic>>()
    }

    test("post story") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.postStory(
            "jwt", "title", "content", "topic",
            isPublished = true,
            commentAllowed = true,
            saveAllowed = true
        ).shouldBeTypeOf<StoryResult.Success<String>>()
    }

    test("fetchStoryDetail") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.fetchStoryDetail("id").shouldBeTypeOf<StoryResult.Success<Story>>()
    }
    test("fetchStoriesByTopic") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.fetchStoriesByTopic("topic", 5, 0)
            .shouldBeInstanceOf<StoryResult<List<SimpleStory>>>()
    }
    test("getSameTopicStoriesWithTarget") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.getSameTopicStoriesWithTarget("sid", 5)
    }
    test("getMyStories") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.getMyStories().shouldBeInstanceOf<StoryResult<List<SimpleStory>>>()
    }
    test("getSavedStories") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.getSavedStories().shouldBeInstanceOf<StoryResult<List<SimpleStory>>>()
    }
    test("saveStory") {
        val storyRepository = StoryRepositoryImpl(authRepo, storyApi)
        storyRepository.saveStory("id", true).shouldBeInstanceOf<StoryResult<Unit>>()
    }

})