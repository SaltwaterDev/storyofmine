package com.unlone.app.domain.entities

// entities
data class ParentDraft(
    val id: String,
    val childDrafts: List<ChildDraft>,
    val topics: List<String> = emptyList(),
)

