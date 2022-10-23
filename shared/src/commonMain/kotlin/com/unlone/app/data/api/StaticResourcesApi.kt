package com.unlone.app.data.api

import com.unlone.app.UnloneConfig
import com.unlone.app.data.story.*
import com.unlone.app.data.write.GuidingQuestionListResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.Charsets


interface StaticResourcesApi {
    suspend fun getGuidingQuestions(): GuidingQuestionListResponse
}

internal class StaticResourcesApiService(httpClientEngine: HttpClientEngine) : StaticResourcesApi {
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


    override suspend fun getGuidingQuestions(): GuidingQuestionListResponse {
        val response = client.get("$baseUrl/guidingQuestions")
        return response.body()
    }

}
