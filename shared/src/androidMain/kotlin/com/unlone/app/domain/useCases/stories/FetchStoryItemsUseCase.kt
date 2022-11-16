package com.unlone.app.domain.useCases.stories

import co.touchlab.kermit.Logger
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.Topic
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

actual class FetchStoryItemsUseCase(
    private val storyRepository: StoryRepository,
    private val topicRepository: TopicRepository,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val pager = Pager(
        clientScope = coroutineScope,
        config = pagingConfig,
        initialKey = 0, // Key to use when initialized
        getItems = { currentKey, size ->

            try {
                // recruit items
                val storiesByTopic = storyRepository.fetchStoriesByPosts(
                    currentKey,
                    postsPerTopic,
                    size
                )

                val randomTopics =
                    if (currentKey == 0) topicRepository.getRandomTopic(randomTopicSize) else null

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
        return pager.pagingData
            .cachedIn(coroutineScope) // cachedIn from AndroidX Paging. on iOS, this is a no-op
    }

    companion object {
        private const val postsPerTopic = 5
        private const val itemsPerPage = 3
        private const val randomTopicSize = 4
        private val pagingConfig =
            PagingConfig(pageSize = itemsPerPage, enablePlaceholders = false)
    }
}
