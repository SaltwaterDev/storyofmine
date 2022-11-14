package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

actual class FetchStoryItemsUseCase(
    private val fetchTopicStoryUseCase: FetchTopicStoryUseCase,
) {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pager = fetchTopicStoryUseCase.pager

    val pagingData: Flow<PagingData<StoryItem.StoriesByTopic>> = pager.pagingData

}
