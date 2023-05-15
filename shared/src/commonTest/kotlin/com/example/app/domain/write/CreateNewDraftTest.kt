package com.example.app.domain.write

import com.example.app.domain.useCases.write.CreateNewDraftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CreateNewDraftUseCaseTest : FunSpec({

    test("test create new draft user case success") {
        val useCase = CreateNewDraftUseCase()
        useCase.invoke().shouldBe(
            mapOf(
                "id" to null,
                "version" to null,
                "title" to "",
                "content" to "",
                "selectedTopic" to "",
            )
        )
    }
})