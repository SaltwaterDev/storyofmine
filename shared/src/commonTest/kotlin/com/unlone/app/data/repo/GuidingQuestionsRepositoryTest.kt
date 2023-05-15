package com.unlone.app.data.repo

import com.unlone.app.data.repo.mockObjects.MockKmmPreference
import com.unlone.app.data.repo.mockObjects.MockStaticResourcesApi
import com.unlone.app.data.userPreference.UserPreferenceRepositoryImpl
import com.unlone.app.data.write.GuidingQuestion
import com.unlone.app.data.write.GuidingQuestionsRepositoryImpl
import com.unlone.app.data.write.StaticResourceResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf

class GuidingQuestionsRepositoryTest : FunSpec({

    val api = MockStaticResourcesApi()
    val pref = MockKmmPreference()
    val userPrefRepo = UserPreferenceRepositoryImpl(pref)

    test("getGuidingQuestionList"){
        val guidingQuestionsRepo = GuidingQuestionsRepositoryImpl(api, userPrefRepo)
        guidingQuestionsRepo.getGuidingQuestionList().shouldBeTypeOf<StaticResourceResult.Success<List<GuidingQuestion>>>()
    }
})