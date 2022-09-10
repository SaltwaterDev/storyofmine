package com.unlone.app.data.story

import com.unlone.app.domain.entities.Story
import com.unlone.app.domain.entities.StoryItem

interface StoryRepository {
    suspend fun fetchStoriesByPosts(
        postPerFetching: Int,
        itemsPerPage: Int,
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

    suspend fun fetchStoryDetail(
        id: String
    ): StoryResult<Story>
}
