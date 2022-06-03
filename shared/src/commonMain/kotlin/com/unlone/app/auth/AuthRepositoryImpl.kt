package com.unlone.app.auth

import com.unlone.app.kermit
import com.unlone.app.utils.KMMPreference
import io.ktor.client.plugins.*

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: KMMPreference,
) : AuthRepository {

    override suspend fun signUp(username: String, password: String): AuthResult<Unit> {
        return try {
            api.signUp(
                request = AuthRequest(
                    username = username,
                    password = password,
                )
            )
            signIn(username, password)
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: Exception) {
            AuthResult.UnknownError()
            // todo
        }
    }

    override suspend fun signIn(username: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.signIn(
                request = AuthRequest(
                    username = username,
                    password = password,
                )
            )
            kermit.d { response.token }
            prefs.put("jwt", response.token)
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: Exception) {
            kermit.e { e.toString() }
            AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt") ?: return AuthResult.Unauthorized()
            api.authenticate("Bearer $token")
            AuthResult.Authorized()
        } catch (e: RedirectResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ClientRequestException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: ServerResponseException) {
            AuthResult.Unauthorized()
            // todo
        } catch (e: Exception) {
            AuthResult.UnknownError()
            // todo
        }
    }
}