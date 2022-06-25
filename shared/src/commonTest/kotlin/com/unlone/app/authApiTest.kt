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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sampleClientTest() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val authApiService = AuthApiService(mockEngine)

            // if it doesn't crash, the test succeed
            authApiService.authenticate("12345")
        }
    }

}