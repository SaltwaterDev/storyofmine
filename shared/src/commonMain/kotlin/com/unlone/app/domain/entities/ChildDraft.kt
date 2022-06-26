package com.unlone.app.domain.entities

// entities
data class ChildDraft(
    val version: String,
    val title: String,
    val content: String,
    val timeStamp: Long
)
