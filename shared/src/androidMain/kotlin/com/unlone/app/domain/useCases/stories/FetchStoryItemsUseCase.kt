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
import com.unlone.app.data.story.Topic
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
                // recruit items
                val storiesByTopic = storyRepository.fetchStoriesByPosts(
                    page = currentKey,
                    postPerTopic = postsPerTopic,
                    itemsPerPage = size
                )

                val randomTopicsResult =
                    if (currentKey == 0) topicRepository.getRandomTopic(randomTopicSize) else null

                // parse randomTopicsResult
                val randomTopics = if (randomTopicsResult is StoryResult.Success) {
                    randomTopicsResult.data
                } else {
                    Logger.e { randomTopicsResult?.errorMsg.toString() }
                    null
                }

                val items = integrateStoryItem(storiesByTopic, randomTopics)
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

    private fun integrateStoryItem(
        storiesByTopic: List<StoryItem.StoriesByTopic>,
        randomTopics: List<Topic>?,
    ): List<StoryItem> {
        val topicTableStoryItem =
            randomTopics?.let {
                listOf(
                    StoryItem.TopicTable(
                        topics = it
                    )
                )
            } ?: emptyList()

        return topicTableStoryItem + storiesByTopic
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
