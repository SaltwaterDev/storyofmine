package com.unlone.app.domain.useCases.stories

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.flatMap
import com.kuuurt.paging.multiplatform.map
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.StoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FetchStoryItemsUseCase(
    private val fetchTopicStoryUseCase: FetchTopicStoryUseCase,
) {
    @NativeCoroutinesIgnore
    operator fun invoke(): Flow<PagingData<StoryItem.StoriesByTopic>> {
        return fetchTopicStoryUseCase()

    }
}
