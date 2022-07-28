package com.unlone.app.domain.useCases.write

import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryDraftUseCase(private val draftRepository: DraftRepository) {
    operator fun invoke(id: String): Flow<Pair<String, DraftVersion>> {
        var shouldUpdate = true
        return draftRepository.queryDraft(id).map {
            if (shouldUpdate) {
                draftRepository.updateLastOpenedTime(it.id)     // update lastOpened prop.
                shouldUpdate = false
            }
            Pair(it.id, it.latestVersion!!)
        }
    }
}