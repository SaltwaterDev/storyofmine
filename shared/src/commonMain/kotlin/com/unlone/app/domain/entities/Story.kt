package com.unlone.app.domain.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val topic: String,
    val author: String,
    val isPublished: Boolean,
    val isSelfWritten: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
    val comment: Comment?,
    val createdDate: String,
    val isSaved: Boolean,
) {
    companion object {
        val mock: Story = Story(
            "id",
            "title",
            "content",
            "topic",
            "author",
            isPublished = true,
            isSelfWritten = true,
            commentAllowed = true,
            saveAllowed = true,
            isSaved = true,
            comment = null,
            createdDate = Clock.System.now().toString(),
        )
    }
}

