package com.unlone.app.data.repo

import com.unlone.app.data.auth.AuthResult
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.launch
import kotlin.test.assertIs


class AuthRepoTest : FunSpec({

    val testAuthRepository = TestAuthRepository()


    test("given Api and KmmPreference Mock when Calling Mocked Method then throw Unknown Error")
        .config(coroutineTestScope = true) {
            launch {
                val result = testAuthRepository.authenticate()
                assertIs<AuthResult.UnknownError<Unit>>(result)
            }
        }

    test("given Api and KmmPreference Mock when Calling Mocked Method then Correctly Verified")
        .config(coroutineTestScope = true) {
            launch {
                val result = testAuthRepository.authenticate()
                assertIs<AuthResult.Authorized<Unit>>(result)
            }
        }
})





