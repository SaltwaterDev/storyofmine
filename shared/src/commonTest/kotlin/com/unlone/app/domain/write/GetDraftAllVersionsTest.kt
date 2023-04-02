package com.unlone.app.domain.write

import com.unlone.app.domain.MockDraftRepository
import com.unlone.app.domain.entities.DraftVersion
import com.unlone.app.domain.useCases.write.GetDraftAllVersionsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.flow.collectLatest


class GetDraftAllVersionsTest: FunSpec ({
    val draftRepo = MockDraftRepository()

    test("get draft all version test"){
        val useCase = GetDraftAllVersionsUseCase(draftRepo)
        useCase.invoke("1234").collectLatest {
            it.shouldBeTypeOf<Pair<String, List<DraftVersion>>>()
        }
    }
})