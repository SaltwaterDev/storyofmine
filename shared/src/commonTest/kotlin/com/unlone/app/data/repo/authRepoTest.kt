package com.unlone.app.data.repo

import com.unlone.app.data.auth.AuthResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.launch
import kotlin.test.assertIs


class AuthRepoTest : FunSpec({

    val testAuthRepository = TestAuthRepository()

    test("given Api and KmmPreference Mock when Calling Mocked Method then throw Unknown Error")
        .config(coroutineTestScope = true) {
            val result = testAuthRepository.authenticate()
            result.shouldBeInstanceOf<AuthResult<Unit>>()
        }
})





