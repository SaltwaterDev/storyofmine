package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.userPreference.UserPreferenceRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface GuidingQuestionsRepository {
    val guidingQuestionList: List<GuidingQuestion>
    suspend fun getGuidingQuestionList(): StaticResourceResult<List<GuidingQuestion>>
}


class GuidingQuestionsRepositoryImpl(
    private val api: StaticResourcesApi,
    private val userPreferenceRepository: UserPreferenceRepository
) : GuidingQuestionsRepository {

    init {
        Logger.addLogWriter(CrashlyticsLogWriter())
    }

    override var guidingQuestionList: List<GuidingQuestion> = listOf()
        private set

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
//            Logger.e { e.toString() }
            Logger.e(e) { "GuidingQuestionsRepositoryImpl error" }
            StaticResourceResult.Failed(errorMsg = e.message)
        }
    }
}

