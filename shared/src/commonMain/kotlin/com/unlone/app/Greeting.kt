package com.unlone.app

import io.ktor.client.*
import io.ktor.client.engine.*


class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}


expect val httpClientEngine: HttpClientEngine