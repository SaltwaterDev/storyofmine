package com.unlone.app.data.auth

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.AuthApi
import com.unlone.app.utils.KMMPreference
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: KMMPreference,
) : AuthRepository {

    init {
        // todo: di coroutine
        CoroutineScope(Dispatchers.Default).launch {
            authenticate()
        }
    }

    override var isUserSignedIn = MutableStateFlow(false)
        private set

    override suspend fun signUp(
        email: String,
        password: String
    ): AuthResult<Unit> {
        return try {
            api.signUp(
                request = AuthRequest(
                    email = email,
                    password = password,
                )
            )
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun signInEmail(email: String): AuthResult<Unit> {
        return try {
            api.validateEmail(
                request = AuthEmailRequest(
                    email = email,
                )
            )
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun signUpEmail(email: String): AuthResult<Unit> {
        return try {
            api.checkEmailExisted(
                request = AuthEmailRequest(
                    email = email,
                )
            )
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.signIn(
                request = AuthRequest(
                    email = email,
                    password = password,
                )
            )
            prefs.put(JWT_SP_KEY, response.token)
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString(JWT_SP_KEY)
            if (token == null) {
                isUserSignedIn.value = false
                AuthResult.Unauthorized<Unit>(null)
            } else {
                api.authenticate(token)
                isUserSignedIn.value = true
                AuthResult.Authorized()
            }
        } catch (e: RedirectResponseException) {
            isUserSignedIn.value = false
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            prefs.remove(JWT_SP_KEY)
            isUserSignedIn.value = false
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            prefs.remove(JWT_SP_KEY)
            isUserSignedIn.value = false
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            isUserSignedIn.value = false
            Logger.e { e.toString() }
            AuthResult.UnknownError()
        }
    }

    override suspend fun requestOtpEmail(email: String): AuthResult<Unit> {
        return try {
            api.requestOtp(email)
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun verifyOtp(email: String, otp: Int): AuthResult<Unit> {
        return try {
            api.verifyOtp(
                AuthOtpRequest(email, otp)
            )
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun signOut() {
        prefs.remove(JWT_SP_KEY)
        authenticate()
    }

    override fun getJwt(): String? {
        return prefs.getString(JWT_SP_KEY)
    }

    override suspend fun setUserName(email: String, username: String): AuthResult<Unit> {
        return try {
            api.setUserName(email, username)
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun getUsername(): AuthResult<String> {
        return try {
            prefs.getString(JWT_SP_KEY)?.let {
                val username = api.getUserName(it)
                Logger.d(username)
                AuthResult.Authorized(username)
            } ?: throw Exception("jwt doesn't exist")
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    override suspend fun removeUserRecordByEmail(email: String): AuthResult<Unit> {
        return try {
            api.removeUserRecord(email)
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: ResponseException) {
            AuthResult.Unauthorized(errorMsg = e.response.body<String>())
            // todo
        } catch (e: Exception) {
            Logger.e(e.toString())
            AuthResult.UnknownError()
        }
    }

    companion object {
        private const val JWT_SP_KEY = "jwt"
    }

}