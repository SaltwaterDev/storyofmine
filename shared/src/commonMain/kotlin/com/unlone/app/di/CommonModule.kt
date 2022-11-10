package com.unlone.app.di

import com.unlone.app.Greeting
import com.unlone.app.data.api.*
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthRepositoryImpl
import com.unlone.app.data.rules.RulesRepository
import com.unlone.app.data.rules.RulesRepositoryImpl
import com.unlone.app.data.story.*
import com.unlone.app.data.userPreference.UserPreferenceRepository
import com.unlone.app.data.userPreference.UserPreferenceRepositoryImpl
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.DraftRepositoryImpl
import com.unlone.app.data.write.GuidingQuestionsRepository
import com.unlone.app.data.write.GuidingQuestionsRepositoryImpl
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.domain.useCases.auth.ValidPasswordUseCase
import com.unlone.app.domain.useCases.stories.FetchStoriesByTopicUseCase
import com.unlone.app.domain.useCases.stories.FetchStoryDetailUseCase
import com.unlone.app.domain.useCases.stories.FetchStoryItemsUseCase
import com.unlone.app.domain.useCases.stories.FetchTopicStoriesItemWithRequestedStoryUseCase
import com.unlone.app.domain.useCases.write.*
import com.unlone.app.httpClientEngine
import com.unlone.app.utils.KMMPreference
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    singleOf(::Greeting)        // as an example

    single { KMMPreference(get()) }

    // data source
    single { httpClientEngine }
    single<AuthApi> { AuthApiService(get()) }
    single<StoryApi> { StoryApiService(get()) }
    single<StaticResourcesApi> { StaticResourcesApiService(get()) }

    // use cases
    singleOf(::CreateNewDraftUseCase)
    singleOf(::FetchStoriesByTopicUseCase)
    singleOf(::FetchTopicStoriesItemWithRequestedStoryUseCase)
    singleOf(::FetchStoryDetailUseCase)
    singleOf(::FetchStoryItemsUseCase)
    singleOf(::GetAllDraftsTitleUseCase)
    singleOf(::GetDraftAllVersionsUseCase)
    singleOf(::GetLatestDraftUseCase)
    singleOf(::GetLastOpenedDraftUseCase)
    singleOf(::PostStoryUseCase)
    singleOf(::QueryDraftUseCase)
    singleOf(::SaveDraftUseCase)
    singleOf(::ValidPasswordUseCase)
    singleOf(::IsUserSignedInUseCase)

    // repositories
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::CommentRepositoryImpl) { bind<CommentRepository>() }
    singleOf(::DraftRepositoryImpl) { bind<DraftRepository>() }
    singleOf(::GuidingQuestionsRepositoryImpl) { bind<GuidingQuestionsRepository>() }
    singleOf(::ReportRepositoryImpl) { bind<ReportRepository>() }
    singleOf(::RulesRepositoryImpl) { bind<RulesRepository>() }
    singleOf(::StoryRepositoryImpl) { bind<StoryRepository>() }
    singleOf(::TopicRepositoryImpl) { bind<TopicRepository>() }
    singleOf(::UserPreferenceRepositoryImpl) { bind<UserPreferenceRepository>() }
}