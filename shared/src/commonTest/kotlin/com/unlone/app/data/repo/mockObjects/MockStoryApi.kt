package com.unlone.app.data.repo.mockObjects

import com.unlone.app.data.api.StoryApi
import com.unlone.app.data.story.*

class MockStoryApi: StoryApi {

    override suspend fun postStory(request: StoryRequest, jwt: String): String {
        return "12345"
    }

    override suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        page: Int,
        topic: String?
    ): StoriesPerTopicsResponse {
        return StoriesPerTopicsResponse(listOf(TopicStoryResponse("topic", listOf(SimpleStory.mock()))))
    }

    override suspend fun getAllTopics(): AllTopicResponse {
        return AllTopicResponse(listOf(Topic("12345", "topic1")))
    }

    override suspend fun getRandomTopics(amount: Int): AllTopicResponse {
        return AllTopicResponse(listOf(Topic("12345", "topic1")))
    }

    override suspend fun fetchStoryDetail(pid: String, token: String): StoryResponse {
        return StoryResponse.mock
    }

    override suspend fun getSameTopicStories(
        requestedStory: String?,
        pagingSize: Int,
        page: Int?
    ): StoriesPerTopicsResponse {
        return StoriesPerTopicsResponse(listOf())
    }

    override suspend fun getReportReasons(lang: String?): ReportReasonResponse {
        return ReportReasonResponse(listOf())
    }

    override suspend fun postReport(reportRequest: ReportRequest, token: String) {
    }

    override suspend fun getComments(storyId: String, token: String): CommentResponse {
        return CommentResponse(listOf())
    }

    override suspend fun postComment(
        commentRequest: CommentRequest,
        token: String
    ): CommentResponse {
        return CommentResponse(listOf())
    }

    override suspend fun getMyStories(token: String): StoriesResponse {
        return StoriesResponse(listOf())
    }

    override suspend fun getSavedStories(token: String): StoriesResponse {
        return StoriesResponse(listOf())
    }

    override suspend fun saveStory(token: String, saveRequest: SaveRequest) {
    }

}