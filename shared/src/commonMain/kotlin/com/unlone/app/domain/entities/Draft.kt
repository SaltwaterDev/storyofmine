package com.unlone.app.domain.entities

import kotlinx.datetime.Instant

// entities
data class Draft(
    val id: String,
    val draftVersions: List<DraftVersion>,
    val lastOpened: Instant
) {
    val latestVersion = draftVersions.maxByOrNull { it.timeStamp }
    companion object {
        val mock = Draft(
            "fakeId",
            List(3) { DraftVersion.mock },
            Instant.DISTANT_PAST,
        )
    }
}

