package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.StoryApi
import io.ktor.client.call.*
import io.ktor.client.plugins.*

interface TopicRepository {
    suspend fun getAllTopic(): StoryResult<List<Topic>>
}


class TopicRepositoryImpl(
    private val storyApi: StoryApi
) : TopicRepository {
    override suspend fun getAllTopic(): StoryResult<List<Topic>> {

        return try {
            val response = storyApi.getAllTopics()
            Logger.d { response.data.toString() }
            StoryResult.Success(response.data)
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.UnknownError(errorMsg = e.message)
        }
    }
}