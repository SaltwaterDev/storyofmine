package com.unlone.app.data.api

import com.unlone.app.data.auth.AuthRequest
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


// todo: still studying
class AuthApiTest : FunSpec({

    val mockEngine = MockEngine {
        // todo: what does it do?
        respond(
            content = ByteReadChannel("""{"id":"iq78r7qyb9","username":"user1234"}"""),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    val authApiService = AuthApiService(mockEngine)

    test("authenticate").config(coroutineTestScope = true) {
        // if it doesn't crash, the test succeed
        authApiService.authenticate("12345")
    }

    test("signUp").config(coroutineTestScope = true) {
        // if it doesn't crash, the test succeed
        authApiService.signUp(AuthRequest(email = "email", password = "password"))
    }
})