package com.example.app.data.repo

import com.example.app.data.repo.mockObjects.MockKmmPreference
import com.example.app.data.repo.mockObjects.MockStaticResourcesApi
import com.example.app.data.userPreference.UserPreferenceRepositoryImpl
import com.example.app.data.write.GuidingQuestion
import com.example.app.data.write.GuidingQuestionsRepositoryImpl
import com.example.app.data.write.StaticResourceResult
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