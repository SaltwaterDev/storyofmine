package com.unlone.app.domain.useCases.write

import com.unlone.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLatestDraftUseCase(private val getDraftAllVersionsUseCase: GetDraftAllVersionsUseCase) {
    operator fun invoke(id: String): Flow<Pair<String, DraftVersion?>> {
        return getDraftAllVersionsUseCase(id).map {
            it.first to it.second.maxByOrNull { it2 -> it2.timeStamp }
        }
    }
}


