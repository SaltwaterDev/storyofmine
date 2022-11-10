package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FetchStoryItemsUseCase(
    private val storyRepository: StoryRepository,
    private val fetchTopicStoriesItemWithRequestedStoryUseCase: FetchTopicStoriesItemWithRequestedStoryUseCase
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var requestStoryId: String? = null

    private val pager = Pager(
        clientScope = coroutineScope,
        config = pagingConfig,
        initialKey = 0, // Key to use when initialized
        getItems = { currentKey, size ->

            var items = storyRepository.fetchStoriesByPosts(currentKey, postsPerTopic, size)

            val requestTopicStoriesResult =
                requestStoryId?.let {
                    fetchTopicStoriesItemWithRequestedStoryUseCase(
                        it,
                        currentKey
                    )
                }
            if (requestTopicStoriesResult is StoryResult.Success) {
                items = requestTopicStoriesResult.data?.let { listOf(it) + items } ?: items
            }
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { null }, // Key for previous page, null means don't load previous pages
                nextKey = { currentKey + (size / itemsPerPage) }
            )
        }
    )

    @NativeCoroutinesIgnore
    operator fun invoke(requestStory: String?): Flow<PagingData<StoryItem.StoriesByTopic>> {
        requestStory?.let { requestStoryId = it }
        return pager.pagingData
            .cachedIn(coroutineScope) // cachedIn from AndroidX Paging. on iOS, this is a no-op
    }


    // region: for ios use
    val pagingData: Flow<PagingData<StoryItem.StoriesByTopic>> =
        pager.pagingData.cachedIn(coroutineScope)

    fun setRequestStory(id: String) {
        requestStoryId = id
    }
    // endregion


    companion object {
        private const val postsPerTopic = 5
        private const val itemsPerPage = 5
        private val pagingConfig = PagingConfig(pageSize = itemsPerPage, enablePlaceholders = false)
    }
}
