package com.unlone.app.data.repo.mockObjects

import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.rules.RulesResponse
import com.unlone.app.data.write.GuidingQuestionListResponse


class MockStaticResourcesApi : StaticResourcesApi {
    override suspend fun getGuidingQuestions(lang: String?): GuidingQuestionListResponse {
        return GuidingQuestionListResponse(listOf())
    }

    override suspend fun getRules(lang: String?): RulesResponse {
        return RulesResponse()
    }

}
