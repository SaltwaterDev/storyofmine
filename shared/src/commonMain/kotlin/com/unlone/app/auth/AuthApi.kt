package com.unlone.app.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


interface AuthApi {
    suspend fun signUp(request: AuthRequest)
    suspend fun checkEmailExisted(request: AuthEmailRequest)
    suspend fun signIn(request: AuthRequest): TokenResponse
    suspend fun validateEmail(request: AuthEmailRequest)
    suspend fun authenticate(token: String)
}

class AuthApiService : AuthApi {
    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
    }


    override suspend fun signUp(request: AuthRequest) {
        client.post(baseUrl + "signup/emailAndPassword") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun checkEmailExisted(request: AuthEmailRequest) {
        client.post(baseUrl + "signup/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }


    override suspend fun validateEmail(request: AuthEmailRequest) {
        client.post(baseUrl + "signin/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }


    override suspend fun signIn(request: AuthRequest): TokenResponse {
        val response = client.post(baseUrl + "signin/emailAndPassword") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }


    override suspend fun authenticate(token: String) {
        client.get(baseUrl + "authenticate") {
            header("Authorization", token)
        }
    }

    companion object {
        // local IP address for running on an emulator
//        private const val baseUrl = "http://10.0.2.2:8080/"
        private const val baseUrl = "http://192.168.8.154:8080/"
//        private const val baseUrl = "https://unlone.an.r.appspot.com/"
    }
}