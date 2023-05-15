package com.example.app.data.story

import com.example.app.domain.entities.Story
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.random.Random


sealed class StoryResult<T>(
    val data: T? = null,
    val errorMsg: String? = null,
    val exception: StoryException? = null
) {
    class Success<T>(data: T? = null) : StoryResult<T>(data = data)

    // todo: remove errorMsg
    class Failed<T>(errorMsg: String? = null, exception: StoryException? = null) :
        StoryResult<T>(errorMsg = errorMsg, exception = exception)

    class UnknownError<T>(errorMsg: String?) : StoryResult<T>(errorMsg = errorMsg)
}

sealed class StoryException : Exception()

sealed class PublishStoryException(override val message: String) : StoryException() {
    class EmptyTopicException(override val message: String = "Topic should not be empty") :
        PublishStoryException(message)

    class EmptyTitleOrBodyException(override val message: String = "Title and body should not be empty") :
        PublishStoryException(message)

}

@Serializable
data class StoriesResponse(
    val data: List<SimpleStory>
)

@Serializable
data class StoryResponse(
    val id: String,
    val title: String,
    val content: String,
    val topic: String,
    val author: String,
    val isPublished: Boolean,
    val isSelfWritten: Boolean,
    val isSaved: Boolean,
    val commentAllowed: Boolean,
    val saveAllowed: Boolean,
    val createdDate: String,
) {
    fun toStory(): Story {
        return Story(
            this.id,
            this.title,
            this.content,
            this.topic,
            this.author,
            this.isPublished,
            this.isSelfWritten,
            this.commentAllowed,
            this.saveAllowed,
            null,
            this.createdDate,
            this.isSaved
        )
    }

    companion object {
        val mock = StoryResponse(
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
            createdDate = Clock.System.now().toString(),
        )
    }
}


@Serializable
data class SimpleStory(
    val id: String,
    val title: String,
    val content: String,
    val topic: String,
    val createdDate: Long?,
) {
    companion object {
        fun mock() = SimpleStory(
            Random.nextLong(0, 10000).toString(),
            "",
            "",
            "",
            null,
        )
    }
}
