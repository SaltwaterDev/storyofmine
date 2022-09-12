package com.unlone.app.domain.auth

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsUserSignedInTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test auth success`() = runTest {
        val authRepo = mockk<AuthRepository>()
        coEvery { authRepo.authenticate() } returns AuthResult.Authorized()

        val useCase = IsUserSignedInUseCase(authRepo)
        assertTrue {
            useCase.invoke()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test unauthorized`() = runTest {
        val authRepo = mockk<AuthRepository>()
        coEvery { authRepo.authenticate() } returns AuthResult.Unauthorized("error")

        val useCase = IsUserSignedInUseCase(authRepo)
        assertFalse {
            useCase.invoke()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test unknown fail`() = runTest {
        val authRepo = mockk<AuthRepository>()
        coEvery { authRepo.authenticate() } returns AuthResult.UnknownError()

        val useCase = IsUserSignedInUseCase(authRepo)
        assertFalse {
            useCase.invoke()
        }
    }
}
