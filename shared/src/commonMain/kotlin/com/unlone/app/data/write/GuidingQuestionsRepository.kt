package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.userPreference.UserPreferenceRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CancellationException

interface GuidingQuestionsRepository {
    suspend fun getGuidingQuestionList(): StaticResourceResult<List<GuidingQuestion>>
}


class GuidingQuestionsRepositoryImpl(
    private val api: StaticResourcesApi,
    private val userPreferenceRepository: UserPreferenceRepository
) : GuidingQuestionsRepository {

    override suspend fun getGuidingQuestionList(): StaticResourceResult<List<GuidingQuestion>> {
        return try {
            val response = api.getGuidingQuestions(
                userPreferenceRepository.getLocale()?.localeName
            )
            StaticResourceResult.Success(response.data)
        } catch (e: RedirectResponseException) {
            StaticResourceResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StaticResourceResult.Failed(errorMsg = e.response.body<String>())
        } catch (_: CancellationException){
            // do nothing
            StaticResourceResult.Failed(null)
        }
        catch (e: Exception) {
            Logger.e(e) { "GuidingQuestionsRepositoryImpl error" }
            StaticResourceResult.UnknownError(errorMsg = e.message)
        }
    }
}

