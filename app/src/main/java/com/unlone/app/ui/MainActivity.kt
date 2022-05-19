package com.unlone.app.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unlone.app.R
import com.unlone.app.data.UserRepository
import com.unlone.app.data.UserRepository.Companion.LOCALE_KEY
import com.unlone.app.data.UserRepository.Companion.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_startup_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navBottomBar = findViewById<BottomNavigationView>(R.id.nav_view)
        navBottomBar.setupWithNavController(navController)

        // hide the bottom navigation bar when they are not lounge and profile
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> navBottomBar.visibility = View.VISIBLE
                R.id.navigation_profile -> navBottomBar.visibility = View.VISIBLE
                else -> navBottomBar.visibility = View.GONE
            }
        }
    }


    override fun attachBaseContext(base: Context) {
        val newBase = runBlocking(Dispatchers.Default) { updateBaseContextLocale(base) }
        super.attachBaseContext(newBase)
    }

    private suspend fun updateBaseContextLocale(context: Context): Context? {
        val language = context.dataStore.data.map { it[LOCALE_KEY] }.first()
        return if (language != null) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                updateResourcesLocale(context, locale)
            } else updateResourcesLocaleLegacy(context, locale)
        } else {
            context.dataStore.edit {
                it[LOCALE_KEY] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales.get(0).toString()
                } else
                    context.resources.configuration.locale.toString()
            }
            context
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private fun updateResourcesLocale(
        context: Context, locale: Locale
    ): Context? {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

}



