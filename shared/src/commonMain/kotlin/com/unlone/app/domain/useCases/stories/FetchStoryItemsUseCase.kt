package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.kuuurt.paging.multiplatform.helpers.dispatcher
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.domain.entities.StoryItem
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FetchStoryItemsUseCase(private val storyRepository: StoryRepository) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val pager = Pager(
        clientScope = coroutineScope,
        config = pagingConfig,
        initialKey = 0, // Key to use when initialized
        getItems = { currentKey, size ->
            val items = storyRepository.fetchStoriesByPosts(currentKey, postsPerPage, size)
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { null }, // Key for previous page, null means don't load previous pages
                nextKey = { if (size == itemsPerPage * 3) currentKey + 3 else currentKey + 1 }
            )
        }
    )


    operator fun invoke(): Flow<PagingData<StoryItem.StoriesByTopic>> {
        return pager.pagingData
            .cachedIn(coroutineScope) // cachedIn from AndroidX Paging. on iOS, this is a no-op
    }

    companion object {
        private const val postsPerPage = 7
        private const val itemsPerPage = 5
        private val pagingConfig = PagingConfig(pageSize = itemsPerPage, enablePlaceholders = false)
    }
}
