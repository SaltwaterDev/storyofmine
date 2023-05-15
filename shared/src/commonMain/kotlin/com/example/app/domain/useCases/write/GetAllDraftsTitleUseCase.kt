package com.example.app.domain.useCases.write

import com.example.app.data.write.DraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllDraftsTitleUseCase(
    private val draftRepository: DraftRepository,
) {

    operator fun invoke(): Flow<Map<String, String>> {
        return draftRepository.getAllDrafts().map {
            it.associate { it1 ->
                it1.id to it1.draftVersions.maxByOrNull { it2 -> it2.timeStamp }!!.title
            }
        }
    }
}