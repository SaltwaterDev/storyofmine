package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.write.StoryApi
import com.unlone.app.domain.entities.StoryItem


internal class StoryRepositoryImpl(private val storyApi: StoryApi) : StoryRepository {
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
}
