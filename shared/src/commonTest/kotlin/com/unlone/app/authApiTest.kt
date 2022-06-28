package com.unlone.app

import com.unlone.app.auth.AuthApiService
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


// todo: still studying
class AuthApiTest {

    private val mockEngine = MockEngine { request ->
        // todo: what does it do?
        respond(
            content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    private val authApiService = AuthApiService(mockEngine)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sampleClientTest() {
        runBlocking {
            // if it doesn't crash, the test succeed
            authApiService.authenticate("12345")
        }
    }



}