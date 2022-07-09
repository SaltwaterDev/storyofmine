package com.unlone.app.data.write

import co.touchlab.kermit.Logger


internal class StoryRepositoryImpl(private val storyApi: StoryApi) : StoryRepository {
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
                    jwt = "Bearer $jwt",
                    title = title,
                    content = content,
                    topic, isPublished, commentAllowed, saveAllowed
                )
            )
            StoryResult.Success()
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }
}
