package com.unlone.app.data

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun setNewLocale(language: String, context: Context = this.context) {
        storeDataStoreNewLocale(context, language)
        val resources = context.resources
        val configuration = resources.configuration
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        configuration.setLocale(Locale(language))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            this.context.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, displayMetrics)
        }
    }


    fun getLocale() = context.dataStore.data.map {
        it[LOCALE_KEY]
    }

    fun getFireStoreLocale() = context.dataStore.data.map {
        Timber.d(it.toString())
        when (it[LOCALE_KEY]) {
            "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
            else -> "default"        // default language (english)
        }
    }


    companion object {
        val LOCALE_KEY = stringPreferencesKey("locale")
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


        suspend fun storeDataStoreNewLocale(context: Context, language: String) {
            context.dataStore.edit { it[LOCALE_KEY] = language }
        }
    }
}