package com.unlone.app.domain.auth

import com.unlone.app.data.repo.TestAuthRepository
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.flow.first

class IsUserSignedInTest : FunSpec({

    val authRepo = TestAuthRepository()

    test("test auth success").config(coroutineTestScope = true) {
        val useCase = IsUserSignedInUseCase(authRepo)
        useCase.invoke().first().shouldBeTrue()
    }
})
