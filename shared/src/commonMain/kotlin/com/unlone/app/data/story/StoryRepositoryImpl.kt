package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.write.StoryApi
import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.entities.StoryItem
import io.ktor.client.call.*
import io.ktor.client.plugins.*


internal class StoryRepositoryImpl(
    private val authRepository: AuthRepository,
    private val storyApi: StoryApi
) : StoryRepository {
    override suspend fun fetchStoriesByPosts(
        postPerFetching: Int,
        pagingItems: Int,
        lastItemId: String?
    ): List<StoryItem.StoriesByTopic> {
        val response = storyApi.fetchStoriesPerPost(
            postPerFetching, pagingItems, lastItemId
        )
        return response.data.map {
            StoryItem.StoriesByTopic(
                it.topic,
                it.stories
            )
        }
    }

    override suspend fun postStory(
        jwt: String,
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<Unit> {
        return try {
            storyApi.postStory(
                StoryRequest(
                    title = title,
                    content = content,
                    topic, isPublished, commentAllowed, saveAllowed
                ),
                jwt = "Bearer $jwt",
            )
            StoryResult.Success()
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }

    override suspend fun fetchStoryDetail(id: String): StoryResult<Story> {
        return try {
            authRepository.getJwt()?.let { jwt ->
                val response = storyApi.fetchStoryDetail(
                    id,
                    "Bearer $jwt"
                )
                StoryResult.Success(response.toStory())
            } ?: StoryResult.Failed("jwt not exists")
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }

    override suspend fun fetchStoriesByTopic(
        topic: String,
        pagingItems: Int,
        page: Int?
    ): StoryResult<List<SimpleStory>> {
        return try {
            val response = storyApi.fetchStoriesByTopic(
                topic, pagingItems, page
            )
            StoryResult.Success(response.data.flatMap {
                it.stories
            })
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }
}
