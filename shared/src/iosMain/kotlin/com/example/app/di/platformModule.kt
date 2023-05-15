package com.example.app.di

import com.example.app.Platform
import com.example.app.utils.KMMContext
import com.example.app.utils.KMMPreference
import com.example.app.utils.KMMPreferenceImpl
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
    single<KMMPreference> { KMMPreferenceImpl(KMMContext()) }
}
