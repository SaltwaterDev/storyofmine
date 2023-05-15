package com.example.app.domain.useCases.write

import co.touchlab.kermit.Logger
import com.example.app.data.write.DraftRepository
import com.example.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetDraftAllVersionsUseCase(private val draftRepository: DraftRepository) {
    operator fun invoke(id: String): Flow<Pair<String, List<DraftVersion>>> {
        return draftRepository.queryDraft(id).map { parent ->
            Logger.d(parent.draftVersions.toString())
            (parent.id to parent.draftVersions)
        }
    }
}