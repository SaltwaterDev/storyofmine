package com.unlone.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.util.*

@HiltAndroidApp
class UnloneApplication : Application() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        super.onCreate()
        val store = PreferenceLocaleStore(this, Locale(LANGUAGE_ZH))
        // you can use this instance for DI or get it via Lingver.getInstance() later on
        val lingver = Lingver.init(this, store)
    }

    companion object RuntimeLocaleChanger {
        val LANGUAGE = intPreferencesKey("language")
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ZH = "zh"
        const val LANGUAGE_HK_COUNTRY = "HK"
    }


}