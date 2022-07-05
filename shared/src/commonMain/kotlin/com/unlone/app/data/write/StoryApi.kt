package com.unlone.app.data.write

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


interface StoryApi {
    suspend fun postStory(request: StoryRequest)
}

class StoryApiService(httpClientEngine: HttpClientEngine) : StoryApi {
    private val client = HttpClient(httpClientEngine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
    }


    override suspend fun postStory(request: StoryRequest) {
        client.post(baseUrl + "story/post") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    companion object {
        // local IP address for running on an emulator
//        private const val baseUrl = "http://10.0.2.2:8080/"
//        private const val baseUrl = "http://192.168.8.154:8080/"
        private const val baseUrl = "https://unlone.an.r.appspot.com/"
    }
}
