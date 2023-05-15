package com.unlone.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class DatabaseDraft(
    @PrimaryKey val pid: String,
    val title: String,
    val content: String,
    val topic: List<String> = emptyList(),
    val createdTimestamp: String,
)

/*
fun PostDetail.asDatabaseDraft(): DatabaseDraft {
    return DatabaseDraft(
        this.pid,
        this.title,
        this.journal,
        this.labels,
        this.createdTimestamp,
    )
}
*/


/*
fun DatabaseDraft.asDraft(): Draft {
    return Post(
        this.title,
        this.imagePath,
        this.journal,
        this.author_uid,
        this.labels,
        this.category,
        this.createdTimestamp,
        this.pid,
        this.comment,
        this.save,
    )
}*/
