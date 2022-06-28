package com.unlone.app.write

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetDraftAllVersionsUseCase(private val draftRepository: DraftRepository) {
    operator fun invoke(id: String): Flow<Map<String, List<ChildDraft>>> {
        return draftRepository.queryDraft(id).map { parent ->
            mapOf(parent.id.toString() to parent.childDrafts)
        }
    }
}