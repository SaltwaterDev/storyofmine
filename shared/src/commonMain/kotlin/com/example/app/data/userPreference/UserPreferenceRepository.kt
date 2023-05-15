package com.example.app.data.userPreference

import com.example.app.utils.KMMPreference
import dev.icerock.moko.resources.desc.StringDesc

interface UserPreferenceRepository {
    fun setLocale(locale: MyStoriesLocale? = null)
    fun getLocale(): MyStoriesLocale?
}

internal class UserPreferenceRepositoryImpl(
    private val prefs: KMMPreference,
) : UserPreferenceRepository {

    override fun setLocale(locale: MyStoriesLocale?) {
        if (locale != null) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(locale.localeName)
            prefs.put("locale", locale.localeName)
        } else {
            StringDesc.localeType = StringDesc.LocaleType.System
            prefs.remove("locale")
        }
    }

    override fun getLocale(): MyStoriesLocale? {
        val localeString = prefs.getString("locale")
        MyStoriesLocale.values().forEach {
            if (localeString == it.localeName) {
                return it
            }
        }
        return null
    }

    init {
        prefs.getString("locale")?.let {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it)
        }?: kotlin.run { setLocale(MyStoriesLocale.En) }
    }
}

enum class MyStoriesLocale(val localeName: String) {
    Zh(localeName = "zh"),
    En(localeName = "en"),
}