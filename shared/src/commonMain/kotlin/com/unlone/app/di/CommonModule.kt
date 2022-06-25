package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.auth.*
import com.unlone.app.utils.KMMPreference
import com.unlone.app.write.DraftRepository
import com.unlone.app.write.DraftRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example

    single { KMMPreference(get()) }

    // data source
    single<AuthApi> { AuthApiService() }

    // use cases
    single { ValidPasswordUseCase() }

    // repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<DraftRepository> { DraftRepositoryImpl() }
}