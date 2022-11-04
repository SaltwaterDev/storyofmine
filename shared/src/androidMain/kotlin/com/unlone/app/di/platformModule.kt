package com.unlone.app.di

import com.unlone.app.Platform
import com.unlone.app.domain.useCases.CheckNetworkStateUseCase
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
    single { CheckNetworkStateUseCase() }
}
