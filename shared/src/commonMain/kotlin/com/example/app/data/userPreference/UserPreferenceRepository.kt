package com.example.app.data.userPreference

import com.example.app.utils.KMMPreference
import dev.icerock.moko.resources.desc.StringDesc

interface UserPreferenceRepository {
    fun setLocale(locale: UnloneLocale? = null)
    fun getLocale(): UnloneLocale?
}

internal class UserPreferenceRepositoryImpl(
    private val prefs: KMMPreference,
) : UserPreferenceRepository {

    override fun setLocale(locale: UnloneLocale?) {
        if (locale != null) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(locale.localeName)
            prefs.put("locale", locale.localeName)
        } else {
            StringDesc.localeType = StringDesc.LocaleType.System
            prefs.remove("locale")
        }
    }

    override fun getLocale(): UnloneLocale? {
        val localeString = prefs.getString("locale")
        UnloneLocale.values().forEach {
            if (localeString == it.localeName) {
                return it
            }
        }
        return null
    }

    init {
        prefs.getString("locale")?.let {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it)
        }?: kotlin.run { setLocale(UnloneLocale.En) }
    }
}

enum class UnloneLocale(val localeName: String) {
    Zh(localeName = "zh"),
    En(localeName = "en"),
}