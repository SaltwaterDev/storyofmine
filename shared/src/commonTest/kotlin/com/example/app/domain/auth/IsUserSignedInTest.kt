package com.example.app.domain.auth

import com.example.app.data.repo.mockObjects.MockAuthRepository
import com.example.app.domain.useCases.auth.IsUserSignedInUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.flow.first

class IsUserSignedInTest : FunSpec({

    val authRepo = MockAuthRepository()

    test("test auth success").config(coroutineTestScope = true) {
        val useCase = IsUserSignedInUseCase(authRepo)
        useCase.invoke().first().shouldBeTrue()
    }
})
