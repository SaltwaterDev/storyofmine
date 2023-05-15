package com.unlone.app.domain.write

import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.MockDraftRepository
import com.unlone.app.domain.entities.Draft
import com.unlone.app.domain.entities.DraftVersion
import com.unlone.app.domain.useCases.write.QueryDraftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class QueryDraftTest : FunSpec({
    val draftRepository: DraftRepository = MockDraftRepository()

    test("QueryDraftTest success").config(coroutineTestScope = true) {
        val useCase = QueryDraftUseCase(draftRepository)
        useCase("1234", "5678").first()
            .shouldBe(Pair(Draft.mock.id, DraftVersion.mock))
    }

    test("QueryDraftTest Without Version success").config(coroutineTestScope = true) {
        val useCase = QueryDraftUseCase(draftRepository)
        useCase("1234", "").first()
            .shouldBe(Pair(Draft.mock.id, DraftVersion.mock))
    }
})