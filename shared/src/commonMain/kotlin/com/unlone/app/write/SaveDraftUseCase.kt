package com.unlone.app.write

class SaveDraftUseCase(private val draftRepository: DraftRepository) {
    operator fun invoke(id: String?, title: String, content: String) {
        draftRepository.saveDraft(id, title, content)
    }
}


