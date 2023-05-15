package com.unlone.app.domain.entities



sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
    class UnknownError(
        val message: String?
    ) : NetworkState()
}
