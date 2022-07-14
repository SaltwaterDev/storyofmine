package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import com.unlone.app.data.story.StoriesPerTopicsResponse
import com.unlone.app.data.story.StoryRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface StoryApi {
    suspend fun postStory(request: StoryRequest, jwt: String)
    suspend fun fetchStoriesPerPost(
        postsPerTopic: Int,
        pagingItems: Int,
        lastItemId: String?
    ): StoriesPerTopicsResponse
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

    companion object {
        //        local IP address for running on an emulator
        private const val baseUrl = "http://10.0.2.2:8080/"
//        private const val baseUrl = "http://192.168.8.154:8080/"
//        private const val baseUrl = "https://unlone.an.r.appspot.com/"
    }
}

@Serializable
data class AllStoriesRequest(
    val postsPerTopic: Int,
    val pagingItems: Int,
    val lastItemId: String? = null,
)
