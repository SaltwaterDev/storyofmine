package com.unlone.app.domain.entities

sealed interface NetworkState {

    object Ok : NetworkState
    object Unavailable : NetworkState
    class UnknownError(
        val message: String?
    ) : NetworkState

}