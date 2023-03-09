package com.unlone.app.domain.write

import com.unlone.app.domain.MockDraftRepository
import com.unlone.app.domain.useCases.write.GetLastOpenedDraftUseCase
import io.kotest.core.spec.style.FunSpec

class GetLastOpenedDraftTest : FunSpec({

    val draftRepo = MockDraftRepository()

    test("test last open draft") {
        val useCase = GetLastOpenedDraftUseCase(draftRepo)
        // todo:
    }
})