package com.unlone.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.unlone.app.model.Post
import com.unlone.app.utils.Converters
import java.util.ArrayList


@Entity
@TypeConverters(Converters::class)
data class DatabasePost(
    val title: String,
    val imagePath: String,
    val journal: String,
    val author_uid: String,
    val labels: List<String> = emptyList(),
    val category: String,
    val createdTimestamp: String,
    @PrimaryKey val pid: String,
    val comment: Boolean,
    val save: Boolean,
)

fun Post.asDatabasePost(): DatabasePost {
    return DatabasePost(
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
}


fun DatabasePost.asPost(): Post {
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
}