package com.example.app.data.repo

import com.example.app.data.api.StaticResourcesApi
import com.example.app.data.repo.mockObjects.MockKmmPreference
import com.example.app.data.repo.mockObjects.MockStaticResourcesApi
import com.example.app.data.rules.Rule
import com.example.app.data.rules.RulesRepositoryImpl
import com.example.app.data.userPreference.UserPreferenceRepositoryImpl
import com.example.app.data.write.StaticResourceResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf


class RulesRepositoryTest : FunSpec({
    val staticResourcesApi: StaticResourcesApi = MockStaticResourcesApi()
    val prefs = MockKmmPreference()
    val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)

    test("getRules") {
        val rulesRepository = RulesRepositoryImpl(staticResourcesApi, userPreferenceRepository)
        rulesRepository.getRules().shouldBeTypeOf<StaticResourceResult.Success<List<Rule>?>>()

    }
})