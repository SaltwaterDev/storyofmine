package com.unlone.app.data.story

import com.unlone.app.domain.entities.StoryItem

interface StoryRepository {
    suspend fun fetchStoriesByPosts(
        postPerFetching: Int,
        pagingItems: Int,
        lastItemId: String?
    ): List<StoryItem.StoriesByTopic>

    suspend fun postStory(
        jwt: String,
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<Unit>
}
