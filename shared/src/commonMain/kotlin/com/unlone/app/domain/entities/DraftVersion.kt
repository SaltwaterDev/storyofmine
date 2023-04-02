package com.unlone.app.domain.entities

import io.ktor.util.date.*

data class DraftVersion(
    val version: String,
    val title: String,
    val content: String,
    val timeStamp: Long
){
    companion object{
        val mock = DraftVersion(
            version = "fakeVersion",
            title = "title",
            "content",
            getTimeMillis(),
        )
    }
}
