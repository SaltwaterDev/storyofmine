package com.example.app.data.repo.mockObjects

import com.example.app.data.api.StaticResourcesApi
import com.example.app.data.rules.RulesResponse
import com.example.app.data.write.GuidingQuestionListResponse


class MockStaticResourcesApi : StaticResourcesApi {
    override suspend fun getGuidingQuestions(lang: String?): GuidingQuestionListResponse {
        return GuidingQuestionListResponse(listOf())
    }

    override suspend fun getRules(lang: String?): RulesResponse {
        return RulesResponse()
    }

}
