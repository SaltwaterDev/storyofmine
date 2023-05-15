package com.example.app.domain.write

import com.example.app.domain.MockDraftRepository
import com.example.app.domain.entities.Draft
import com.example.app.domain.useCases.write.GetAllDraftsTitleUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class GetAllDraftsTitleTest : FunSpec({

    val draftRepo = MockDraftRepository()

    test("get all draft").config(coroutineDebugProbes = true) {
        val useCase = GetAllDraftsTitleUseCase(draftRepo)

        useCase.invoke().first().shouldBe(
            mapOf(Draft.mock.id to "title")
        )
    }
})
