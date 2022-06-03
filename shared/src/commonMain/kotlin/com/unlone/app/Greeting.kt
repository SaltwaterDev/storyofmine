package com.unlone.app

import co.touchlab.kermit.Kermit

val kermit = Kermit()

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}