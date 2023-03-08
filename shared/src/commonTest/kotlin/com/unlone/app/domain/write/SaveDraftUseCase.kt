package com.unlone.app.domain.write

import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.MockTestDraftRepository
import com.unlone.app.domain.useCases.write.SaveDraftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class SaveDraftTest : FunSpec({
    val draftRepo = MockTestDraftRepository()

    test("Save new draft success") {
        val useCase = SaveDraftUseCase(draftRepo)
        useCase.invoke("", "title", "body")
            .shouldBeTypeOf<StoryResult.Success<Pair<String, String>?>>()
    }

    test("Update draft success") {
        val useCase = SaveDraftUseCase(draftRepo)
        useCase.invoke("1234", "title", "body")
            .shouldBeTypeOf<StoryResult.Success<Pair<String, String>?>>()
    }

    test("Create Draft Empty Title and Body") {
        val useCase = SaveDraftUseCase(draftRepo)
        val result = useCase.invoke("", "", "")
        result.shouldBeTypeOf<StoryResult.Failed<Pair<String, String>?>>()
        result.errorMsg.shouldBe("title and content is blank")
    }

    test("Update Draft Empty Title and Body") {
        val useCase = SaveDraftUseCase(draftRepo)
        val result = useCase.invoke("1234", "", "")
        result.shouldBeTypeOf<StoryResult.Failed<Pair<String, String>?>>()
        result.errorMsg.shouldBe("title and content is blank")
    }
})