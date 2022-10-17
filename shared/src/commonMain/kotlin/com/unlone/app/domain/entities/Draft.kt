package com.unlone.app.domain.entities

import kotlinx.datetime.Instant

// entities
data class Draft(
    val id: String,
    val draftVersions: List<DraftVersion>,
    val lastOpened: Instant
){
    val latestVersion = draftVersions.maxByOrNull { it.timeStamp }
}

