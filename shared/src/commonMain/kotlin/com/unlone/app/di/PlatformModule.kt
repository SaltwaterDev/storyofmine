package com.unlone.app.di

import com.unlone.app.Platform
import com.unlone.app.utils.KMMContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val platformModule = module {
    single { Platform() }
//    single { KMMContext() }
}