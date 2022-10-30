package com.unlone.app.data.rules

import kotlinx.serialization.Serializable

@Serializable
data class RulesResponse(
    val data: List<Rules> = listOf(Rules())
)

@Serializable
data class Rules(
    val id: String = "",
    val text: String = ""
)
