package com.unlone.app.di

import com.unlone.app.Platform
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
}
