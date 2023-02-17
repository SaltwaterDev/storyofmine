package com.unlone.app.domain.entities

data class DraftVersion(
    val version: String,
    val title: String,
    val content: String,
    val timeStamp: Long
)
