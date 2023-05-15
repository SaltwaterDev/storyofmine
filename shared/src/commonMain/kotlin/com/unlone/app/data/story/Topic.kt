package com.unlone.app.data.story

import kotlinx.serialization.Serializable


@Serializable
data class Topic(
    val id: String,
    val name: String,
)


@Serializable
data class AllTopicResponse(
    val data: List<Topic>,
)
