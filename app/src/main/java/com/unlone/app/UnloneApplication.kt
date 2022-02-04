package com.unlone.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class UnloneApplication : Application() {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        super.onCreate()
        val store = PreferenceLocaleStore(this, Locale(LANGUAGE_ENGLISH))
        // you can use this instance for DI or get it via Lingver.getInstance() later on
        val lingver = Lingver.init(this, store)
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ZH = "zh"
        const val LANGUAGE_HK_COUNTRY = "HK"
    }
}