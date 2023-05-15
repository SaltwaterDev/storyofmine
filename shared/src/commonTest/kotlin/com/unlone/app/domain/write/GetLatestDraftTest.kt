package com.unlone.app.domain.write

import com.unlone.app.domain.MockDraftRepository
import com.unlone.app.domain.entities.DraftVersion
import com.unlone.app.domain.useCases.write.GetDraftAllVersionsUseCase
import com.unlone.app.domain.useCases.write.GetLatestDraftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.flow.first

class GetLatestDraftTest: FunSpec({
    val draftRepo = MockDraftRepository()
    val getDraftAllVersionsUseCase = GetDraftAllVersionsUseCase(draftRepo)

    test("test get latest draft"){
        val useCase = GetLatestDraftUseCase(getDraftAllVersionsUseCase)
        useCase("12345").first().shouldBeTypeOf<Pair<String, DraftVersion?>>()
    }
})
