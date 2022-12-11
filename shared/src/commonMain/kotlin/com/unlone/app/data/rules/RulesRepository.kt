package com.unlone.app.data.rules

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.userPreference.UserPreferenceRepository
import com.unlone.app.data.write.StaticResourceResult
import io.ktor.client.call.*
import io.ktor.client.plugins.*

interface RulesRepository {
    suspend fun getRules(): StaticResourceResult<List<Rules>?>
}

class RulesRepositoryImpl(
    private val staticResourcesApi: StaticResourcesApi,
    private val userPreferenceRepository: UserPreferenceRepository
) : RulesRepository {
    override suspend fun getRules(): StaticResourceResult<List<Rules>?> {
        return try {
            val response = staticResourcesApi.getRules(userPreferenceRepository.getLocale()?.localeName)
            StaticResourceResult.Success(response.data)
        } catch (e: RedirectResponseException) {
            StaticResourceResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StaticResourceResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StaticResourceResult.Failed(errorMsg = e.message)
        }
    }

}