package com.unlone.app

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}