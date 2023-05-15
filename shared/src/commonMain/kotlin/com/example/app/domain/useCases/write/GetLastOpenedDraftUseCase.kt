package com.example.app.domain.useCases.write

import com.example.app.data.write.DraftRepository
import com.example.app.domain.entities.DraftVersion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetLastOpenedDraftUseCase(private val draftRepository: DraftRepository) {

    suspend operator fun invoke(): Pair<String, DraftVersion>? {
        return draftRepository.getLastOpenedDraft().map {
            it?.let { it1 ->
                val latestDraft = it.draftVersions.maxByOrNull { it2 -> it2.timeStamp }!!
                Pair(it1.id, latestDraft)
            }
        }.first()
    }
}