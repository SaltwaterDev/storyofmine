package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.auth.*
import com.unlone.app.httpClientEngine
import com.unlone.app.utils.KMMPreference
import com.unlone.app.write.*
import io.ktor.client.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example

    single { KMMPreference(get()) }

    // data source
    single { httpClientEngine }
    single<AuthApi> { AuthApiService(get()) }

    // use cases
    single { ValidPasswordUseCase() }
    single { GetAllDraftsTitleUseCase(get()) }
    single { GetDraftAllVersionsUseCase(get()) }
    single { GetLatestDraftUseCase(get()) }
    single { GetLastEditedDraftUseCase(get()) }
    single { SaveDraftUseCase(get()) }

    // repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<DraftRepository> { DraftRepositoryImpl() }
}