package com.example.app.di

import com.example.app.Greeting
import com.example.app.data.userPreference.UserPreferenceRepository
import com.example.app.data.userPreference.UserPreferenceRepositoryImpl
import com.example.app.data.write.DraftRepository
import com.example.app.data.write.DraftRepositoryImpl
import com.example.app.domain.useCases.write.*
import com.example.app.httpClientEngine
import com.example.app.utils.KMMPreference
import com.example.app.utils.KMMPreferenceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example
    single<KMMPreference> { KMMPreferenceImpl(get()) }

    // data source
    single { httpClientEngine }
    // use cases
    singleOf(::CreateNewDraftUseCase)
    singleOf(::GetAllDraftsTitleUseCase)
    singleOf(::GetDraftAllVersionsUseCase)
    singleOf(::GetLatestDraftUseCase)
    singleOf(::GetLastOpenedDraftUseCase)
    singleOf(::QueryDraftUseCase)
    singleOf(::SaveDraftUseCase)

    // repositories
    singleOf(::DraftRepositoryImpl) { bind<DraftRepository>() }

    singleOf(::UserPreferenceRepositoryImpl) {
        bind<UserPreferenceRepository>()
        createdAtStart()
    }
}