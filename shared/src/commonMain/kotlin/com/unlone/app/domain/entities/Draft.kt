package com.unlone.app.domain.entities

// entities
data class Draft(
    val id: String,
    val draftVersions: List<DraftVersion>,
    val topics: List<String> = emptyList(),     // todo: move to Story entity
){
    val latestVersion = draftVersions.maxByOrNull { it.timeStamp }
}

