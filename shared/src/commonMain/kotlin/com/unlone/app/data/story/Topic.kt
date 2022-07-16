package com.unlone.app.data.story

import kotlinx.serialization.Serializable


@Serializable
data class Topic(
    val id: String,
    val name: String,
)
