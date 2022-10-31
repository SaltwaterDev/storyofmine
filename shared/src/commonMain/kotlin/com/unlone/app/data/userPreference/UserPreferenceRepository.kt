package com.unlone.app.data.userPreference

import com.unlone.app.utils.KMMPreference
import dev.icerock.moko.resources.desc.StringDesc

interface UserPreferenceRepository {
    fun setLocale(locale: String? = null)
    fun getLocale(): String?
}

internal class UserPreferenceRepositoryImpl(
    private val prefs: KMMPreference,
) : UserPreferenceRepository {
    override fun setLocale(locale: String?) {
        if (locale != null) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(locale)
            prefs.put("locale", locale)
        } else{
            StringDesc.localeType = StringDesc.LocaleType.System
            prefs.remove("locale")
        }
    }

    override fun getLocale(): String? {
        return prefs.getString("locale")
    }

    init {
        getLocale()?.let {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it)
        }
    }
}