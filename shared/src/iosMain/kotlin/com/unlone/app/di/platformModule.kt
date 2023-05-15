package com.unlone.app.di

import com.unlone.app.Platform
import com.unlone.app.domain.useCases.stories.FetchStoryItemsUseCase
import com.unlone.app.utils.KMMContext
import com.unlone.app.utils.KMMPreference
import com.unlone.app.utils.KMMPreferenceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


actual val platformModule = module {
    single { Platform() }
    single<KMMPreference> { KMMPreferenceImpl(KMMContext()) }
    singleOf(::FetchStoryItemsUseCase)
}
