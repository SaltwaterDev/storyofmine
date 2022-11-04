package com.unlone.app.domain.useCases

import com.unlone.app.UnloneConfig
import com.unlone.app.domain.entities.NetworkState
import java.net.InetAddress

actual class CheckNetworkStateUseCase {

    actual operator fun invoke(): NetworkState {
        return try {
            NetworkState.Ok
//            val ipAddr = InetAddress.getByName(UnloneConfig.baseUrl);
//            //You can replace it with your name
//            if (!ipAddr.equals("")){
//                NetworkState.Ok
//            } else{
//                NetworkState.Unavailable
//            }

        } catch (e: Exception) {
            NetworkState.UnknownError(e.message)
        }
    }
}