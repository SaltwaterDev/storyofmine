package com.unlone.app.domain.write

import com.unlone.app.domain.MockTestDraftRepository
import com.unlone.app.domain.entities.Draft
import com.unlone.app.domain.useCases.write.GetAllDraftsTitleUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class GetAllDraftsTitleTest : FunSpec({

    val draftRepo = MockTestDraftRepository()

    test("get all draft").config(coroutineDebugProbes = true) {
        val useCase = GetAllDraftsTitleUseCase(draftRepo)

        useCase.invoke().first().shouldBe(
            mapOf(Draft.mock.id to "title")
        )
    }
})
