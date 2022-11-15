package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

actual class FetchStoryItemsUseCase(
    private val storyRepository: StoryRepository,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val pager = Pager(
        clientScope = coroutineScope,
        config = pagingConfig,
        initialKey = 0, // Key to use when initialized
        getItems = { currentKey, size ->

            // recruit items
            val storiesByTopic = storyRepository.fetchStoriesByPosts(
                currentKey,
                postsPerTopic,
                size
            )


            val items = integrateStoryItem(currentKey, storiesByTopic)
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { null }, // Key for previous page, null means don't load previous pages
                nextKey = { currentKey + (size / itemsPerPage) }
            )
        }
    )

    private fun integrateStoryItem(
        currentKey: Int,
        storiesByTopic: List<StoryItem.StoriesByTopic>,
    ): List<StoryItem.StoriesByTopic> {
        return storiesByTopic
    }


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @NativeCoroutinesIgnore
    operator fun invoke(): Flow<PagingData<StoryItem.StoriesByTopic>> {
        return pager.pagingData
            .cachedIn(coroutineScope) // cachedIn from AndroidX Paging. on iOS, this is a no-op
    }

    companion object {
        private const val postsPerTopic = 5
        private const val itemsPerPage = 5
        private val pagingConfig =
            PagingConfig(pageSize = itemsPerPage, enablePlaceholders = false)
    }
}
