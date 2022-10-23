package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.story.StoryResult
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface GuidingQuestionsRepository {
    val guidingQuestionList: List<GuidingQuestion>
    suspend fun getGuidingQuestionList(): StaticResourceResult<List<GuidingQuestion>>
}


class GuidingQuestionsRepositoryImpl(
    private val api: StaticResourcesApi
) : GuidingQuestionsRepository {
    override var guidingQuestionList: List<GuidingQuestion> = listOf()
        private set

//    init {
//         todo: di the coroutine scope and context
//        CoroutineScope(Dispatchers.Default).launch {
//            guidingQuestionList = getGuidingQuestionList()
//            guidingQuestionList = api.getGuidingQuestions()
//        }
//    }

    override suspend fun getGuidingQuestionList(): StaticResourceResult<List<GuidingQuestion>> {
        return try {
            val response = api.getGuidingQuestions()
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

    companion object {
        private val mockGuidingQsList = listOf(
            GuidingQuestion("Is there anything in the past that is related to the current issue?"),
            GuidingQuestion("Why do you think this is important to you?"),
            GuidingQuestion("How do you feel? Any bodily sensations? What do these feelings mean to you? "),
            GuidingQuestion("Does it give insight / implications on your personality?"),
            GuidingQuestion("What have you learnt from it? Can it be applied to other aspects in life?"),
            GuidingQuestion("What is your reaction to this? Do you usually react the same way in daily life?"),
            GuidingQuestion("What are your needs and wants reflected from this issue?"),
            GuidingQuestion("What can be done better?"),
            GuidingQuestion("What have you done to stop it from getting worse?"),
            GuidingQuestion("Do you have any alternatives for this?"),
        )
    }

}

