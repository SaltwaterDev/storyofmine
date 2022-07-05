package com.unlone.app.data.write

import co.touchlab.kermit.Logger


class StoryRepositoryImpl(private val storyApi: StoryApi) : StoryRepository {
    override suspend fun postStory(
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
                )
            )
            StoryResult.Success()
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }
}



class StoryRepositoryMockImpl() : StoryRepository {
    override suspend fun postStory(
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<Unit> {
        return StoryResult.Success()
    }
}