package com.unlone.app.ui.access

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.unlone.app.R
import com.unlone.app.data.UserRepository.Companion.LOCALE_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


@AndroidEntryPoint
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_startup)

    }
}