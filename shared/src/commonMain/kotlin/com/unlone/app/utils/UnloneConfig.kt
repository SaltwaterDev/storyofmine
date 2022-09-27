package com.unlone.app.utils


expect val unloneConfig: UnloneConfig

sealed interface UnloneConfig {
    val baseUrl: String

    object Development: UnloneConfig{
        override val baseUrl: String
            get() = "https://unlone.an.r.appspot.com"
    }

    object Staging: UnloneConfig{
        override val baseUrl: String
            get() = "https://unlone.an.r.appspot.com"
    }

}

