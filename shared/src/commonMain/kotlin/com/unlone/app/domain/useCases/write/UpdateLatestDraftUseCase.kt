package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository

class UpdateLatestDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(
        id: String,
        title: String,
        body: String,
    ): StoryResult<Pair<String, String>?> {
        return try {
            if (title.isNotBlank() || body.isNotBlank()) {
                draftRepository.updateDraftVersion(id, title, body)
                StoryResult.Success()
            } else {
                StoryResult.Failed(errorMsg = "title or content is blank")
            }
        } catch (e: Throwable) {
            StoryResult.UnknownError(errorMsg = e.message)
        }
    }
}