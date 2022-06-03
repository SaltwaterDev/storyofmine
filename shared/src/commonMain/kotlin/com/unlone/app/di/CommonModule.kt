package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.auth.AuthApi
import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthRepositoryImpl
import com.unlone.app.utils.KMMPreference
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example
    single { AuthApi() }
    single { KMMPreference(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}