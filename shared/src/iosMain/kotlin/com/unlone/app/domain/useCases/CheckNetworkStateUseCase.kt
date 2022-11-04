package com.unlone.app.domain.useCases

import com.unlone.app.domain.entities.NetworkState


actual class CheckNetworkStateUseCase {

    actual operator fun invoke(): NetworkState {
        // todo
        return NetworkState.Ok
    }
}