package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.PagingData
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.flow.Flow

actual class FetchStoryItemsUseCase(
    private val fetchTopicStoryUseCase: FetchTopicStoryUseCase,
) {
    @NativeCoroutinesIgnore
    operator fun invoke(): Flow<PagingData<StoryItem.StoriesByTopic>> {
        return fetchTopicStoryUseCase()

    }

    // region: for ios use
//    val pagingData: Flow<PagingData<StoryItem.StoriesByTopic>> =
//        pager.pagingData.cachedIn(coroutineScope)

//    fun setRequestStory(id: String) {
//        requestStoryId = id
//    }
    // endregion

}
