package com.unlone.app.domain.write

import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryDraftUseCase(private val draftRepository: DraftRepository) {
    operator fun invoke(id: String, version: String? = null): Flow<Pair<String, DraftVersion>> {
        var shouldUpdate = true
        return draftRepository.queryDraft(id).map { draft ->
            if (shouldUpdate) {
                draftRepository.updateLastOpenedTime(draft.id)     // update lastOpened prop.
                shouldUpdate = false
            }
            version?.let { ver ->
                Pair(
                    draft.id,
                    draft.draftVersions.find { it.version == ver } ?: draft.latestVersion!!)
            } ?: Pair(draft.id, draft.latestVersion!!)
        }
    }
}