package com.unlone.app.data.write

import com.unlone.app.data.story.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable


interface StoryApi {
    suspend fun postStory(request: StoryRequest, jwt: String)
    suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        lastItemId: String?
    ): StoriesPerTopicsResponse

    suspend fun getAllTopics(): AllTopicResponse
    suspend fun fetchStoryDetail(pid: String, token: String): StoryResponse
}

internal class StoryApiService(httpClientEngine: HttpClientEngine) : StoryApi {
    private val client = HttpClient(httpClientEngine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
    }


    override suspend fun postStory(request: StoryRequest, jwt: String) {
        client.post(baseUrl + "story/post") {
            contentType(ContentType.Application.Json)
            header("Authorization", jwt)
            setBody(request)
        }
    }

    override suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        lastItemId: String?
    ): StoriesPerTopicsResponse {
        val response = client.get(baseUrl + "story/allStories") {
            url {
                parameters.append("postsPerTopic", postsPerTopic.toString())
                parameters.append("pagingItems", pagingItems.toString())
                lastItemId?.let { parameters.append("pagingItems", it) }
            }
        }
        return response.body()
    }

    override suspend fun getAllTopics(): AllTopicResponse {
        val response = client.get(baseUrl + "story/allTopics")
        return response.body()
    }

    override suspend fun fetchStoryDetail(pid: String, token: String): StoryResponse {
        val response = client.get(baseUrl + "story/$pid") {
            header("Authorization", token)
        }
        return response.body()
    }


    companion object {
//                local IP address for running on an emulator
//        private const val baseUrl = "http://10.0.2.2:8080/"
//        private const val baseUrl = "http://192.168.8.154:8080/"
        private const val baseUrl = "https://unlone.an.r.appspot.com/"
    }
}
