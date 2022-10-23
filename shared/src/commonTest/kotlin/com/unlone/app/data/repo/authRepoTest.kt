package com.unlone.app.data.repo

import com.unlone.app.data.api.AuthApi
import com.unlone.app.data.auth.AuthRepositoryImpl
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.utils.KMMPreference
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs


@ExperimentalCoroutinesApi
class AuthRepoTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }


    @Test
    fun `given Api and KmmPreference Mock when Calling Mocked Method then throw Unknown Error`() =
        runTest {
            val pref: KMMPreference = mockk {
                every { getString(any()) } returns "13579"
            }
            val api: AuthApi = mockk() {
                coEvery { authenticate(any()) } throws Exception()
            }
            val authRepositoryImpl = AuthRepositoryImpl(api, pref)

            launch {
                val result = authRepositoryImpl.authenticate()
                assertIs<AuthResult.UnknownError<Unit>>(result)
            }
        }

    @Test
    fun `given Api and KmmPreference Mock when Calling Mocked Method then Correctly Verified`() =
        runTest {
            val pref: KMMPreference = mockk {
                every { getString(any()) } returns "13579"
            }
            val api: AuthApi = mockk {
                coEvery { authenticate(any()) } returns Unit
            }
            val authRepositoryImpl = AuthRepositoryImpl(api, pref)

            launch {
                val result = authRepositoryImpl.authenticate()
                assertIs<AuthResult.Authorized<Unit>>(result)
            }
        }
}




