package com.unlone.app.data.story

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