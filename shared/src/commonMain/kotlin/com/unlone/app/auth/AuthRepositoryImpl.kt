package com.unlone.app.auth

import com.unlone.app.kermit
import com.unlone.app.utils.KMMPreference
import io.ktor.client.call.*
import io.ktor.client.plugins.*

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: KMMPreference,
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        username: String,
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
            // signIn(email, password)
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
            kermit.e { e.toString() }
            AuthResult.UnknownError()
            // todo
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
            kermit.d { response.token }
            prefs.put("jwt", response.token)
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
            kermit.e { e.toString() }
            AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt")
                ?: return AuthResult.Unauthorized(null)  // todo： return error Msg (e.g. token expired/ invalid)
            api.authenticate("Bearer $token")
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
        } catch (e: Exception) {
            AuthResult.UnknownError()
            // todo
        }
    }
}