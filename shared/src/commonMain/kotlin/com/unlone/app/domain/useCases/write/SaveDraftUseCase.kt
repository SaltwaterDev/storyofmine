package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(
        id: String?,
        title: String,
        body: String,
        shouldCreateNewVersion: Boolean,
    ): StoryResult<Pair<String, String>?> {
        return if (title.isNotBlank() || body.isNotBlank()) {
            if (shouldCreateNewVersion) {
                draftRepository.saveDraft(id, title, body).let {
                    StoryResult.Success(data = it)
                }
            } else {
                if (id != null) {
                    draftRepository.updateDraftVersion(id, title, body).let {
                        StoryResult.Success()
                    }
                } else {
                    StoryResult.Failed(errorMsg = "id is null")
                }
            }
        } else {
            StoryResult.Failed(errorMsg = "title or content is blank")
        }
    }
}