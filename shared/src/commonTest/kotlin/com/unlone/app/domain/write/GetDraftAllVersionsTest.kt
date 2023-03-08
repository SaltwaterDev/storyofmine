package com.unlone.app.domain.write

import co.touchlab.kermit.Logger
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.MockTestDraftRepository
import com.unlone.app.domain.entities.DraftVersion
import com.unlone.app.domain.useCases.write.GetDraftAllVersionsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map


class GetDraftAllVersionsTest: FunSpec ({
    val draftRepo = MockTestDraftRepository()

    test("get draft all version test"){
        val useCase = GetDraftAllVersionsUseCase(draftRepo)
        useCase.invoke("1234").collectLatest {
            it.shouldBeTypeOf<Pair<String, List<DraftVersion>>>()
        }
    }
})