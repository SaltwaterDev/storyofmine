package com.unlone.app.domain.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.MockDraftRepository
import com.unlone.app.domain.useCases.write.UpdateLatestDraftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class UpdateLatestDraftTest: FunSpec({
    val draftRepo = MockDraftRepository()

    test("Update Latest Draft Success"){
        val useCase = UpdateLatestDraftUseCase(draftRepo)
        useCase.invoke("1234", "title", "body").shouldBeTypeOf<StoryResult.Success<Unit>>()
    }

    test("Update Latest Draft Empty Title or Body"){
        val useCase = UpdateLatestDraftUseCase(draftRepo)
        useCase.invoke("1234", "", "").shouldBeTypeOf<StoryResult.Failed<Unit>>()
    }
})