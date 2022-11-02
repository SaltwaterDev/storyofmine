package com.unlone.app.domain.useCases.stories

import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.Story
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class FetchStoryDetailUseCase(private val storyRepository: StoryRepository) {

    suspend operator fun invoke(id: String): StoryResult<Story> {
        val result = storyRepository.fetchStoryDetail(id)
        return if (result is StoryResult.Failed)
            result
        else {
            val story = result.data?.let {
                val localDateTime = Instant.parse(it.createdDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val createdDate =
                    "${localDateTime.month.name} ${localDateTime.dayOfMonth}  |  ${localDateTime.hour}:${localDateTime.minute}"
                Story(
                    it.id,
                    it.title,
                    it.content,
                    it.topic,
                    it.author,
                    it.isPublished,
                    it.isSelfWritten,
                    it.commentAllowed,
                    it.saveAllowed,
                    it.comment,
                    createdDate,
                    it.isSaved,
                )
            }
            story?.let { StoryResult.Success(it) } ?: result
        }
    }
}