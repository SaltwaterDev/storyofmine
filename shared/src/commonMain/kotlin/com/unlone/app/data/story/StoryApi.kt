package com.unlone.app.data.story

import com.unlone.app.UnloneConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.charsets.Charsets
import org.koin.test.mock.MockProvider.register


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
        topic: String,
        pagingSize: Int,
        page: Int?
    ): StoriesPerTopicsResponse

    suspend fun getReportReasons(): ReportReasonResponse
    suspend fun postReport(
        reportRequest: ReportRequest,
        token: String
    )

    suspend fun getComments(storyId: String, token: String): CommentResponse
    suspend fun postComment(commentRequest: CommentRequest, token: String): CommentResponse
}

internal class StoryApiService(httpClientEngine: HttpClientEngine) : StoryApi {
    private val client = HttpClient(httpClientEngine) {
        expectSuccess = true
        developmentMode = true
        install(ContentNegotiation) {
            json()
        }
        install(ContentEncoding){
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
            header("Authorization", jwt)
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
            header("Authorization", token)
        }
        return response.body()
    }

    override suspend fun fetchStoriesByTopic(
        topic: String,
        pagingSize: Int,
        page: Int?
    ): StoriesPerTopicsResponse {
        val response = client.get("$baseUrl/story/allStoriesFromTopic") {
            url {
                parameters.append("topic", topic)
                parameters.append("pagingSize", pagingSize.toString())
                page?.let { parameters.append("page", page.toString()) }
            }
        }
        return response.body()
    }

    override suspend fun getReportReasons(): ReportReasonResponse {
        val response = client.get("$baseUrl/report/allReportReasons")
        return response.body()
    }

    override suspend fun postReport(
        reportRequest: ReportRequest,
        token: String
    ) {
        client.post("$baseUrl/report/createReport") {
            contentType(ContentType.Application.Json)
            header("Authorization", token)
            setBody(reportRequest)
        }
    }

    override suspend fun getComments(storyId: String, token: String): CommentResponse {
        val response = client.get("$baseUrl/comment/getComments") {
            header("Authorization", token)
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
            header("Authorization", token)
            setBody(commentRequest)
        }
        return response.body()
    }
}
