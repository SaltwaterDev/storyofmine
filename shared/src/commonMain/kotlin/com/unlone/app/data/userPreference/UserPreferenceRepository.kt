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
            StringDesc.localeType = StringDesc.LocaleType.Custom(locale.name)
            prefs.put("locale", locale.name)
        } else {
            StringDesc.localeType = StringDesc.LocaleType.System
            prefs.remove("locale")
        }
    }

    override fun getLocale(): UnloneLocale {
        return when (prefs.getString("locale")) {
            "zh" -> UnloneLocale.Zh
            "en" -> UnloneLocale.En
            else -> UnloneLocale.En
        }
    }

    init {
        prefs.getString("locale")?.let {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it)
        }
    }
}

sealed class UnloneLocale(val name: String) {
    object Zh : UnloneLocale(name = "zh")
    object En : UnloneLocale(name = "en")
}