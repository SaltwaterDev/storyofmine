package com.unlone.app.domain.useCases.write

import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLastOpenedDraftUseCase(private val draftRepository: DraftRepository) {

    operator fun invoke(): Flow<Pair<String, DraftVersion>?> {
        return draftRepository.getLastOpenedDraft().map {
            it?.let { it1 ->
                val latestDraft = it.draftVersions.maxByOrNull { it2 -> it2.timeStamp }!!
                Pair(it1.id, latestDraft)
            }
        }
    }
}