package com.unlone.app.domain.write

import com.unlone.app.domain.MockTestDraftRepository
import com.unlone.app.domain.useCases.write.GetLastOpenedDraftUseCase
import io.kotest.core.spec.style.FunSpec

class GetLastOpenedDraftTest : FunSpec({

    val draftRepo = MockTestDraftRepository()

    test("test last open draft") {
        val useCase = GetLastOpenedDraftUseCase(draftRepo)
        // todo:
    }
})