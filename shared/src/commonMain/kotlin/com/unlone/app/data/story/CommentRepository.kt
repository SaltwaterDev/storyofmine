package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.domain.entities.Comment
import io.ktor.client.call.*
import io.ktor.client.plugins.*

interface CommentRepository {
    suspend fun getComments(sid: String): StoryResult<List<Comment>>
    suspend fun postComment(sid: String, text: String): StoryResult<List<Comment>>
}

class CommentRepositoryImpl(
    private val authRepository: AuthRepository,
    private val storyApi: StoryApi
) : CommentRepository {
    override suspend fun getComments(sid: String): StoryResult<List<Comment>> {
        val jwt = authRepository.getJwt()
        return try {
            val response = storyApi.getComments(sid, "Bearer $jwt")
            StoryResult.Success(
                response.data.map {
                    Comment(
                        it.id,
                        it.author,
                        it.text,
                        it.createdTime
                    )
                }
            )
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.UnknownError(errorMsg = e.message)
        }
    }

    override suspend fun postComment(sid: String, text: String): StoryResult<List<Comment>> {
        val jwt = authRepository.getJwt()
        return try {
            val request = CommentRequest(sid, text)
            val response = storyApi.postComment(request, "Bearer $jwt")
            StoryResult.Success(
                response.data.map {
                    Comment(
                        it.id,
                        it.author,
                        it.text,
                        it.createdTime
                    )
                })
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