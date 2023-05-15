package com.unlone.app.data.rules

import kotlinx.serialization.Serializable

@Serializable
data class RulesResponse(
    val data: List<Rule> = listOf(Rule())
)

@Serializable
data class Rule(
    val id: String = "",
    val text: String = ""
)
