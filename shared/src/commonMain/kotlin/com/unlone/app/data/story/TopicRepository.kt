package com.unlone.app.data.story

import com.unlone.app.data.write.StoryApi

interface TopicRepository {
    suspend fun getAllTopic(): List<Topic>
}


class TopicRepositoryImpl(
    private val storyApi: StoryApi
) : TopicRepository {
    override suspend fun getAllTopic(): List<Topic> {
        return storyApi.getAllTopics().data
    }

}