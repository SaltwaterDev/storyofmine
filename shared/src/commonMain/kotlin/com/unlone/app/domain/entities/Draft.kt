package com.unlone.app.domain.entities

// entities
data class Draft(
    val id: String,
    val draftVersions: List<DraftVersion>,
){
    val latestVersion = draftVersions.maxByOrNull { it.timeStamp }
}

