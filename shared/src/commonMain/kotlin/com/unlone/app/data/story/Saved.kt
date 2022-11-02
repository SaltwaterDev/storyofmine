package com.unlone.app.data.story

@kotlinx.serialization.Serializable
data class SaveRequest(
    val storyId: String,
    val isSaving: Boolean
)

