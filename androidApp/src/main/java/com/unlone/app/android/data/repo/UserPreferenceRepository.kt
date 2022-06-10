package com.unlone.app.android.data.repo

import android.content.Context
import android.os.Build

class UserPreferenceRepository(
    private val context: Context,
) {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }

    fun getFireStoreLocale(): String {
        return when (locale.toString()) {
            "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
            else -> "default"        // default language (english)
        }
    }
}