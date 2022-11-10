package com.unlone.app.data.api

import com.unlone.app.UnloneConfig
import com.unlone.app.data.story.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.Charsets


interface StoryApi {
    suspend fun postStory(request: StoryRequest, jwt: String)
    suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        page: Int,
    ): StoriesPerTopicsResponse

    suspend fun getAllTopics(): AllTopicResponse
    suspend fun fetchStoryDetail(pid: String, token: String): StoryResponse
    suspend fun fetchStoriesByTopic(
        topic: String? = null,
        requestedStory: String? = null,
        pagingSize: Int,
        page: Int?
    ): StoriesPerTopicsResponse

    suspend fun getReportReasons(lang: String?): ReportReasonResponse
    suspend fun postReport(
        reportRequest: ReportRequest,
        token: String
    )

    suspend fun getComments(storyId: String, token: String): CommentResponse
    suspend fun postComment(commentRequest: CommentRequest, token: String): CommentResponse

    suspend fun getMyStories(token: String): StoriesResponse
    suspend fun getSavedStories(token: String): StoriesResponse

    suspend fun saveStory(token: String, saveRequest: SaveRequest)
}


internal class StoryApiService(httpClientEngine: HttpClientEngine) : StoryApi {
    private val client = HttpClient(httpClientEngine) {
        expectSuccess = true
        developmentMode = true
        install(ContentNegotiation) {
            json()
        }
        install(ContentEncoding) {
            gzip()
        }
        Charsets {

            // Allow using `UTF_8`.
            register(Charsets.UTF_8)
            sendCharset = Charsets.UTF_8
        }
    }

    private val serverUrl = UnloneConfig.baseUrl

    // use when running the local backend server
    private val localBaseUrlForEmulator = "http://10.0.2.2:8080"
    private val localBaseUrl = "http://192.168.8.154:8080"
    private val baseUrl = serverUrl


    override suspend fun postStory(request: StoryRequest, jwt: String) {
        client.post("$baseUrl/story/post") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $jwt")
            setBody(request)
        }
    }

    override suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        page: Int,
    ): StoriesPerTopicsResponse {
        val response = client.get("$baseUrl/story/allStories") {
            url {
                parameters.append("postsPerTopic", postsPerTopic.toString())
                parameters.append("itemsPerPage", pagingItems.toString())
                parameters.append("page", page.toString())
            }
        }
        return response.body()
    }

    override suspend fun getAllTopics(): AllTopicResponse {
        val response = client.get("$baseUrl/story/allTopics")
        return response.body()
    }

    override suspend fun fetchStoryDetail(pid: String, token: String): StoryResponse {
        val response = client.get("$baseUrl/story/$pid") {
            header("Authorization", "Bearer $token")
        }
        return response.body()
    }

    override suspend fun fetchStoriesByTopic(
        topic: String?,
        requestedStory: String?,
        pagingSize: Int,
        page: Int?
    ): StoriesPerTopicsResponse {
        val response = client.get("$baseUrl/story/allStoriesFromTopic") {
            url {
                topic?.let { it1 -> parameters.append("topic", it1) }
                requestedStory?.let { it1 -> parameters.append("requestedStory", it1) }
                parameters.append("pagingSize", pagingSize.toString())
                page?.let { parameters.append("page", page.toString()) }
            }
        }
        return response.body()
    }

    override suspend fun getReportReasons(lang: String?): ReportReasonResponse {
        val response = client.get("$baseUrl/report/allReportReasons/${lang}")
        return response.body()
    }

    override suspend fun postReport(
        reportRequest: ReportRequest,
        token: String
    ) {
        client.post("$baseUrl/report/createReport") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(reportRequest)
        }
    }

    override suspend fun getComments(storyId: String, token: String): CommentResponse {
        val response = client.get("$baseUrl/comment/getComments") {
            header("Authorization", "Bearer $token")
            url { parameters.append("storyId", storyId) }
        }
        return response.body()
    }

    override suspend fun postComment(
        commentRequest: CommentRequest,
        token: String
    ): CommentResponse {
        val response = client.post("$baseUrl/comment/postComment") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(commentRequest)
        }
        return response.body()
    }

    override suspend fun getMyStories(token: String): StoriesResponse {
        val response = client.get("$baseUrl/story/myStories") {
            header("Authorization", "Bearer $token")
        }
        return response.body()
    }

    override suspend fun getSavedStories(token: String): StoriesResponse {
//        return StoriesResponse(listOf())
        val response = client.get("$baseUrl/story/saved") {
            header("Authorization", "Bearer $token")
        }
        return response.body()
    }

    override suspend fun saveStory(
        token: String,
        saveRequest: SaveRequest
    ) {
        client.post("$baseUrl/story/save") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(saveRequest)
        }
    }
}
