package com.unlone.app.di

import com.unlone.app.Platform
import com.unlone.app.domain.useCases.CheckNetworkStateUseCase
import com.unlone.app.utils.KMMContext
import com.unlone.app.utils.KMMPreference
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
    single { KMMPreference(KMMContext()) }
    single { CheckNetworkStateUseCase() }
}
