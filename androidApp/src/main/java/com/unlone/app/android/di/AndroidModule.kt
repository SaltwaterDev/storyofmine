package com.unlone.app.android.di

import com.unlone.app.android.viewmodel.*
import com.unlone.app.android.viewmodel.StoryDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val androidModule = module {
    viewModelOf(::WritingViewModel)
    viewModelOf(::StoryDetailViewModel)
    viewModelOf(::TopicDetailViewModel)
    viewModelOf(::StoriesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SignInViewModel)
}