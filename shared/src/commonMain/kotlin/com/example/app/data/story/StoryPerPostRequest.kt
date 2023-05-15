package com.example.app.data.story

import kotlinx.serialization.Serializable


@Serializable
internal data class StoryPerPostRequest(
    val postPerFetching: Int,
    val pagingItems: Int
)
