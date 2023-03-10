package com.unlone.app.domain.useCases.stories

import co.touchlab.kermit.Logger
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

actual class FetchStoryItemsUseCase(
    private val storyRepository: StoryRepository,
    private val topicRepository: TopicRepository,
) {
    private val scope = MainScope()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pager = Pager(
        clientScope = scope,
        config = pagingConfig,
        initialKey = 0, // Key to use when initialized
        getItems = { currentKey, size ->

            try {
                val prioritisedTopicStories: List<StoryItem.StoriesByTopic>? =
                    if (currentKey == 0) getPrioritisedTopicStories() else null
                val randomTopics = if (currentKey == 0) getRandomTopics() else null
                val storiesByTopic = getNormalTopicStories(currentKey, size)

                val items =
                    integrateStoryItem(randomTopics, storiesByTopic, prioritisedTopicStories)

                PagingResult(
                    items = items,
                    currentKey = currentKey,
                    prevKey = { null }, // Key for previous page, null means don't load previous pages
                    nextKey = { currentKey + (size / itemsPerPage) }
                )
            } catch (e: Exception) {
                Logger.e { e.toString() }
                PagingResult(
                    items = listOf(StoryItem.UnknownError(e.toString())),
                    currentKey = currentKey,
                    prevKey = { null }, // Key for previous page, null means don't load previous pages
                    nextKey = { currentKey + (size / itemsPerPage) }
                )
            }
        }
    )

    private suspend fun getRandomTopics(): StoryItem.TopicTable? {
        val randomTopicsResult = topicRepository.getRandomTopic(randomTopicSize)
        return if (randomTopicsResult is StoryResult.Success) {
            randomTopicsResult.data?.let { StoryItem.TopicTable(it) }
        } else {
            Logger.e { randomTopicsResult.errorMsg.toString() }
            null
        }
    }

    private suspend fun getNormalTopicStories(
        page: Int,
        size: Int
    ): List<StoryItem.StoriesByTopic> {
        return storyRepository.fetchStoriesByPosts(
            page = page,
            postPerTopic = postsPerTopic,
            itemsPerPage = size
        )

    }

    private suspend fun getPrioritisedTopicStories(): List<StoryItem.StoriesByTopic>? {
        val storyId = storyRepository.fetchPrioritiseTopicStoriesRepresentative()
        return storyId?.let {
            when (val result =
                storyRepository.getSameTopicStoriesWithTarget(it, postsPerTopic)
            ) {
                is StoryResult.Success -> {
                    result.data
                }
                else -> {
                    Logger.e(result.errorMsg.toString())
                    null
                }
            }
        }?.map {
            StoryItem.StoriesByTopic(it.topic, it.stories)
        }
    }


    private fun integrateStoryItem(
        randomTopics: StoryItem.TopicTable?,
        topicStories: List<StoryItem.StoriesByTopic>,
        prioritisedTopicStories: List<StoryItem.StoriesByTopic>?,
    ): List<StoryItem> {
        val storyItemList = mutableListOf<StoryItem>()
        prioritisedTopicStories?.let { storyItemList.addAll(it) }
        randomTopics?.let { storyItemList.add(it) }

        val distinctStoryItems =
            topicStories.filter { topicStory ->
                prioritisedTopicStories?.all { topicStory.topic != it.topic } ?: true
            }
        storyItemList.addAll(distinctStoryItems)

        return storyItemList
    }


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @NativeCoroutinesIgnore
    operator fun invoke(): Flow<PagingData<StoryItem>> {
        return pager.pagingData.cachedIn(scope)
    }

    companion object {
        private const val postsPerTopic = 4
        private const val itemsPerPage = 4
        private const val randomTopicSize = 4
        private val pagingConfig =
            PagingConfig(pageSize = itemsPerPage, enablePlaceholders = false)
    }
}
