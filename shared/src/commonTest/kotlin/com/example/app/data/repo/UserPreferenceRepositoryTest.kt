package com.example.app.data.repo

import com.example.app.data.repo.mockObjects.MockKmmPreference
import com.example.app.data.userPreference.UserPreferenceRepositoryImpl
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull


internal class UserPreferenceRepositoryTest: FunSpec({

    val prefs = MockKmmPreference()

    test("setLocale"){
        val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)
        userPreferenceRepository.setLocale()
        userPreferenceRepository.setLocale(com.example.app.data.userPreference.MyStoriesLocale.Zh)
        userPreferenceRepository.setLocale(com.example.app.data.userPreference.MyStoriesLocale.En)
    }

    test("getLocale"){
        val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)
        userPreferenceRepository.getLocale().shouldBeNull()
    }
})