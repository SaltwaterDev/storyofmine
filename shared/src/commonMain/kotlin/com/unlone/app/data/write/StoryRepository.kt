package com.unlone.app.data.write


interface StoryRepository {
    suspend fun postStory(
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<Unit>
}