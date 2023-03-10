package com.unlone.app.data.repo

import com.unlone.app.data.repo.mockObjects.MockKmmPreference
import com.unlone.app.data.userPreference.UserPreferenceRepositoryImpl
import com.unlone.app.utils.KMMPreferenceImpl
import dev.icerock.moko.resources.desc.StringDesc
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull


internal class UserPreferenceRepositoryTest: FunSpec({

    val prefs = MockKmmPreference()

    test("setLocale"){
        val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)
        userPreferenceRepository.setLocale()
        userPreferenceRepository.setLocale(com.unlone.app.data.userPreference.UnloneLocale.Zh)
        userPreferenceRepository.setLocale(com.unlone.app.data.userPreference.UnloneLocale.En)
    }

    test("getLocale"){
        val userPreferenceRepository = UserPreferenceRepositoryImpl(prefs)
        userPreferenceRepository.getLocale().shouldBeNull()
    }
})