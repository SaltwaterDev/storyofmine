package com.example.app

import io.ktor.client.engine.*


class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}


expect val httpClientEngine: HttpClientEngine