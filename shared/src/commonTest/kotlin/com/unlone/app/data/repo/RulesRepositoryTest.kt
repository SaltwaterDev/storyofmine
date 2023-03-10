package com.unlone.app.data.repo

import com.unlone.app.data.api.StaticResourcesApi
import com.unlone.app.data.repo.mockObjects.MockKmmPreference
import com.unlone.app.data.repo.mockObjects.MockStaticResourcesApi
import com.unlone.app.data.rules.Rules
import com.unlone.app.data.rules.RulesRepositoryImpl
import com.unlone.app.data.userPreference.UserPreferenceRepositoryImpl
import com.unlone.app.data.write.StaticResourceResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf


class RulesRepositoryTest : FunSpec({
    val staticResourcesApi: StaticResourcesApi = MockStaticResourcesApi()
    val prefs = MockKmmPreference()
    val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)

    test("getRules") {
        val rulesRepository = RulesRepositoryImpl(staticResourcesApi, userPreferenceRepository)
        rulesRepository.getRules().shouldBeTypeOf<StaticResourceResult.Success<List<Rules>?>>()

    }
})