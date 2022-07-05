package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.data.auth.*
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthRepositoryImpl
import com.unlone.app.data.write.*
import com.unlone.app.domain.useCases.ValidPasswordUseCase
import com.unlone.app.httpClientEngine
import com.unlone.app.utils.KMMPreference
import com.unlone.app.domain.useCases.write.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example

    single { KMMPreference(get()) }

    // data source
    single { httpClientEngine }
    single<AuthApi> { AuthApiService(get()) }
    single<StoryApi> { StoryApiService(get()) }

    // use cases
    single { ValidPasswordUseCase() }
    single { GetAllDraftsTitleUseCase(get()) }
    single { GetDraftAllVersionsUseCase(get()) }
    single { GetLatestDraftUseCase(get()) }
    single { GetLastEditedDraftUseCase(get()) }
    single { SaveDraftUseCase(get()) }
    single { QueryDraftUseCase(get()) }

    // repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<DraftRepository> { DraftRepositoryImpl() }
    single<StoryRepository> { StoryRepositoryImpl(get()) }
}