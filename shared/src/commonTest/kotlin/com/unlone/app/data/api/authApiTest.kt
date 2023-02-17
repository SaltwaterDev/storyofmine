package com.unlone.app.data.api

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


// todo: still studying
class AuthApiTest: FunSpec({

    val mockEngine = MockEngine { request ->
        // todo: what does it do?
        respond(
            content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    val authApiService = AuthApiService(mockEngine)


    test("sampleClientTest") {
        runBlocking {
            // if it doesn't crash, the test succeed
            authApiService.authenticate("12345")
        }
    }
})