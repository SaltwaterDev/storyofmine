package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.api.StoryApi

interface TopicRepository {
    suspend fun getAllTopic(): List<Topic>
}


class TopicRepositoryImpl(
    private val storyApi: StoryApi
) : TopicRepository {
    override suspend fun getAllTopic(): List<Topic> {
        val topics = storyApi.getAllTopics().data
        Logger.d { topics.toString() }
        return topics
    }

}