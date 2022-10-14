package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(id: String?, title: String, content: String): StoryResult<String?> {
        return if (title.isNotBlank()) {
            draftRepository.saveDraft(id, title, content)?.let {
                StoryResult.Success(data = it)
            } ?: StoryResult.Failed(errorMsg = "Saving Draft Failed")
        } else {
            StoryResult.Failed(errorMsg = "title is blank")
        }
    }
}