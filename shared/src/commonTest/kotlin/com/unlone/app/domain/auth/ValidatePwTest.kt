package com.unlone.app.domain.auth

import com.unlone.app.domain.useCases.auth.ValidPasswordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidatePwTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate pw success`() = runTest {
        val useCase = ValidPasswordUseCase()
        assertTrue {
            useCase.invoke("1234QWEe")
            useCase.invoke("1234QWEe!1!")
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate pw fail`() = runTest {
        val useCase = ValidPasswordUseCase()
        assertFalse { useCase.invoke("") }
        assertFalse { useCase.invoke("12341234") }
        assertFalse { useCase.invoke("1123Aa") }
        assertFalse { useCase.invoke("1243aaaa") }
        assertFalse { useCase.invoke("1243aaaa") }
    }
}
