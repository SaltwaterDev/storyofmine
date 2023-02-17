package com.unlone.app.domain.auth

import com.unlone.app.data.repo.TestAuthRepository
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import io.kotest.core.spec.style.FunSpec

class IsUserSignedInTest: FunSpec({

    val authRepo = TestAuthRepository()

    test ("test auth success")
        .config(coroutineTestScope = true) {
        val useCase = IsUserSignedInUseCase(authRepo)
        useCase.invoke() shouldBe
//        assertTrue {
//            useCase.invoke()
//        }
    }

    test ("test unauthorized")
        .config(coroutineTestScope = true) {
        val useCase = IsUserSignedInUseCase(authRepo)
//        assertFalse {
//            useCase.invoke()
//        }
    }

    test ("test unknown fail")
        .config(coroutineTestScope = true) {
        val useCase = IsUserSignedInUseCase(authRepo)
//        assertFalse {
//            useCase.invoke()
//        }
    }
})
