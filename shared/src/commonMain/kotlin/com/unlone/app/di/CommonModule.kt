package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.data.userPreference.UserPreferenceRepository
import com.unlone.app.data.userPreference.UserPreferenceRepositoryImpl
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.DraftRepositoryImpl
import com.unlone.app.domain.useCases.write.*
import com.unlone.app.httpClientEngine
import com.unlone.app.utils.KMMPreference
import com.unlone.app.utils.KMMPreferenceImpl
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