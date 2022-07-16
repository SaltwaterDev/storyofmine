package com.unlone.app.domain.useCases.write

class CreateNewDraftUseCase {
    operator fun invoke(): Map<String, String?> {
        return mapOf(
            "id" to null,
            "title" to "",
            "content" to "",
            "selectedTopic" to "",
        )
    }
}