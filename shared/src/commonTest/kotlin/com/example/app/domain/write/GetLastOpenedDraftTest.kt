package com.example.app.domain.write

import com.example.app.domain.MockDraftRepository
import com.example.app.domain.useCases.write.GetLastOpenedDraftUseCase
import io.kotest.core.spec.style.FunSpec

class GetLastOpenedDraftTest : FunSpec({

    val draftRepo = MockDraftRepository()

    test("test last open draft") {
        val useCase = GetLastOpenedDraftUseCase(draftRepo)
        // todo:
    }
})