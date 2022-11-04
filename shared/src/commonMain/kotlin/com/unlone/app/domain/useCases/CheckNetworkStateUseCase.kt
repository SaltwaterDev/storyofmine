package com.unlone.app.domain.useCases

import com.unlone.app.domain.entities.NetworkState

expect class CheckNetworkStateUseCase {

    operator fun invoke(): NetworkState
}

