package com.example.app.data.api

import com.example.app.data.auth.AuthEmailRequest
import com.example.app.data.auth.AuthOtpRequest
import com.example.app.data.auth.AuthRequest
import com.example.app.data.auth.TokenResponse
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

class AuthApiTest : FunSpec({

    test("signUp success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondOk() }
        val authApiService = AuthApiService(mockEngine)
        authApiService.signUp(AuthRequest(email = "email", password = "password"))
    }

    test("signUp fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.signUp(AuthRequest(email = "email", password = "password"))
        }
    }

    test("checkEmailExisted success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondOk("some json response") }
        val authApiService = AuthApiService(mockEngine)
        authApiService.checkEmailExisted(AuthEmailRequest(email = "email"))
    }

    test("checkEmailExisted fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.checkEmailExisted(AuthEmailRequest(email = "email"))
        }
    }

    test("validateEmail success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondOk("some json response") }
        val authApiService = AuthApiService(mockEngine)
        authApiService.validateEmail(AuthEmailRequest(email = "email"))
    }

    test("validateEmail fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.validateEmail(AuthEmailRequest(email = "email"))
        }
    }

    test("signIn success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"token":"iq78r7qyb9"}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val authApiService = AuthApiService(mockEngine)
        val tokenResponse = authApiService.signIn(AuthRequest(email = "email", password = "pw"))

        val tokenResponseMatcher = Matcher<TokenResponse> { value ->
            MatcherResult(
                value == TokenResponse(token = "iq78r7qyb9"),
                { "Token Response $value should be iq78r7qyb9" },
                { "Token Response $value should be iq78r7qyb9" },
            )
        }
        tokenResponse.shouldBe(tokenResponseMatcher)
    }

    test("signIn fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.signIn(AuthRequest(email = "email", password = "pw"))
        }
    }

    test("authenticate success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"id":"iq78r7qyb9","username":"user1234"}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.authenticate("12345")
    }

    test("authenticate fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.authenticate("12345")
        }
    }

    test("requestOtp success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondOk()
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.requestOtp("12345@emai.com")
    }

    test("requestOtp fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.requestOtp("12345@emai.com")
        }
    }

    test("verifyOtp success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondOk()
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.verifyOtp(AuthOtpRequest(email = "abc@email.com", otp = 12345))
    }

    test("verifyOtp fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.verifyOtp(AuthOtpRequest(email = "abc@email.com", otp = 12345))
        }
    }

    test("setUserName success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondOk()
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.setUserName(email = "abc@email.com", username = "user")
    }

    test("setUserName fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.setUserName(email = "abc@email.com", username = "user")
        }
    }

   test("getUserName success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond("user")
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.getUserName(token = "12345").shouldBe("user")
    }

    test("getUserName fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.getUserName(token = "12345")
        }
    }

    test("removeUserRecord success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondOk()
        }
        val authApiService = AuthApiService(mockEngine)
        authApiService.removeUserRecord(email = "abc@email.com")
    }

    test("removeUserRecord fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val authApiService = AuthApiService(mockEngine)
        shouldThrowAny {
            authApiService.removeUserRecord(email = "abc@email.com")
        }
    }

})