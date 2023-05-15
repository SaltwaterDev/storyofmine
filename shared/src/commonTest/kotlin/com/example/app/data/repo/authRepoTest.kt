package com.example.app.data.repo

import com.example.app.data.auth.AuthResult
import com.example.app.data.repo.mockObjects.MockAuthApi
import com.example.app.data.repo.mockObjects.MockKmmPreference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class AuthRepoTest : FunSpec({

    val kmmPref = MockKmmPreference()
    val apiTest = MockAuthApi()


    test("test authenticate success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.authenticate()
            result.shouldBeInstanceOf<AuthResult<Unit>>()
        }

    test("test signUp success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.signUp("email", "password")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("test signIn success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.signIn("email", "password")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("test signInEmail success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.signInEmail("email")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("test requestOtpEmail success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.requestOtpEmail("email")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("test signUpEmail success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.signUpEmail("email")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("test verifyOtp success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.verifyOtp("email", 12345)
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }

    test("tests signOut") {
        val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
        authRepo.signOut()
    }

    test("test setUserName success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.setUserName("email", "username")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }


    test("test removeUserRecordByEmail success")
        .config(coroutineTestScope = true) {
            val authRepo = AuthRepositoryImpl(apiTest, kmmPref)
            val result = authRepo.removeUserRecordByEmail("email")
            result.shouldBeInstanceOf<AuthResult.Authorized<Unit>>()
        }
})





