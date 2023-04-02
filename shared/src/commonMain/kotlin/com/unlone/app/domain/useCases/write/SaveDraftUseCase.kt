package com.unlone.app.domain.useCases.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(
        draftId: String?,
        title: String,
        body: String,
        shouldCreateNewVersion: Boolean,
    ): StoryResult<Pair<String, String>?> {
        if (title.isBlank() && body.isBlank()) return StoryResult.Failed(errorMsg = "title and content is blank")

        return if (draftId == null) {
            val result = draftRepository.createNewDraft(title, body)
            StoryResult.Success(data = result)

        } else if (shouldCreateNewVersion) {
            val result = draftRepository.addNewVersionToDraft(draftId, title, body)
            if (result.first != null && result.second != null)
                StoryResult.Success(data = Pair(result.first!!, result.second!!))
            else
                StoryResult.Failed(errorMsg = "result draftId or Version is null")

        } else {
            val result = draftRepository.updateDraftVersion(draftId, title, body)
            StoryResult.Success(data = result)

        }
    }
}