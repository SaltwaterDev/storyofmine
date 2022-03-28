package com.unlone.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class UnloneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }
}
