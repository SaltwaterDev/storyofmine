package com.unlone.app.domain.useCases.write

import co.touchlab.kermit.Logger
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.NetworkState
import com.unlone.app.domain.useCases.CheckNetworkStateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetAllDraftsTitleUseCase(
    private val draftRepository: DraftRepository,
    private val networkStateUseCase: CheckNetworkStateUseCase,
) {

    operator fun invoke(): Flow<Map<String, String>> {
        return if (networkStateUseCase() !is NetworkState.Ok) {
            Logger.e { "network unavailable" }
            flowOf(mapOf())
        } else {
            draftRepository.getAllDrafts().map {
                it.associate { it1 ->
                    it1.id to it1.draftVersions.maxByOrNull { it2 -> it2.timeStamp }!!.title
                }
            }
        }
    }
}