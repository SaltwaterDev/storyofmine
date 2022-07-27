package com.unlone.app.domain.useCases.write

import com.unlone.app.data.write.DraftRepository

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    suspend operator fun invoke(id: String?, title: String, content: String) {
        if (title.isNotBlank()) {
            draftRepository.saveDraft(id, title, content)
        }
    }
}