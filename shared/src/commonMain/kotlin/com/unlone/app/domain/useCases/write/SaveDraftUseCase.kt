package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(
        id: String?,
        title: String,
        body: String,
    ): StoryResult<Pair<String, String>?> {
        return if (title.isNotBlank() || body.isNotBlank()) {
            draftRepository.saveDraft(id, title, body).let {
                StoryResult.Success(data = it)
            }
        } else {
            StoryResult.Failed(errorMsg = "title or content is blank")
        }
    }
}