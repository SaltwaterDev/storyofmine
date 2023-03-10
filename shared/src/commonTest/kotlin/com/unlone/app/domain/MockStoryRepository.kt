package com.unlone.app.domain

import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicStoryResponse
import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.entities.StoryItem

class MockStoryRepository : StoryRepository {
    override suspend fun fetchStoriesByPosts(
        page: Int,
        postPerTopic: Int,
        itemsPerPage: Int
    ): List<StoryItem.StoriesByTopic> {
        return listOf()
    }

    override suspend fun postStory(
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean
    ): StoryResult<String> {
        return StoryResult.Success("storyId")
    }

    override suspend fun fetchStoryDetail(id: String): StoryResult<Story> {
        return StoryResult.Success(Story.mock)
    }

    override suspend fun fetchStoriesByTopic(
        topic: String?,
        pagingItems: Int,
        page: Int?
    ): StoryResult<List<SimpleStory>> {
        return StoryResult.Success(List(3){ SimpleStory.mock()})
    }

    override suspend fun getSameTopicStoriesWithTarget(
        requestedStory: String,
        storiesPerTopic: Int
    ): StoryResult<List<TopicStoryResponse>> {
        return StoryResult.Success(listOf(TopicStoryResponse("topic", listOf(SimpleStory.mock()))))
    }

    override suspend fun getMyStories(): StoryResult<List<SimpleStory>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSavedStories(): StoryResult<List<SimpleStory>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveStory(storyId: String, save: Boolean): StoryResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setPrioritiseTopicStoriesRepresentative(storyId: String) {
    }

    override suspend fun fetchPrioritiseTopicStoriesRepresentative(): String? {
        return "234"
    }
}