package com.unlone.app.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


class AuthApi {
    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
    }


    suspend fun signUp(request: AuthRequest) {
        client.post(baseUrl + "signup/emailAndPassword") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun signUpEmail(request: AuthEmailRequest) {
        client.post(baseUrl + "signup/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }






    suspend fun signIn(request: AuthRequest): TokenResponse {
        val response = client.post(baseUrl + "signin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }





    suspend fun authenticate(token: String) {
        client.get(baseUrl + "authenticate") {
            header("Authorization", token)
        }
    }

    companion object {
        // local IP address for running on an emulator
        private const val baseUrl = "http://10.0.2.2:8080/"
    }
}