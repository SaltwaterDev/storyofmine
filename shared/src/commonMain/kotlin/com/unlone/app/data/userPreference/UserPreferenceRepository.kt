package com.unlone.app.data.userPreference

import com.unlone.app.utils.KMMPreference
import dev.icerock.moko.resources.desc.StringDesc
import org.example.library.SharedRes

interface UserPreferenceRepository {
    fun setLocale(locale: UnloneLocale? = null)
    fun getLocale(): UnloneLocale
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

    override fun getLocale(): UnloneLocale {
        val localeString = prefs.getString("locale")
        UnloneLocale.values().forEach {
            if (localeString == it.localeName) {
                return it
            }
        }
        return UnloneLocale.En
    }

    init {
        prefs.getString("locale")?.let {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it)
        }
    }
}

enum class UnloneLocale(val localeName: String) {
    Zh(localeName = "zh"),
    En(localeName = "en")
}