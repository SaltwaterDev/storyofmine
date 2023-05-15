package com.example.app.di

import com.example.app.Platform
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
}
