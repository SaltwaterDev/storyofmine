package com.unlone.app.android.di

import com.unlone.app.android.viewmodel.*
import com.unlone.app.android.viewmodel.StoryDetailViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val androidModule = module {
    viewModelOf(::EditHistoryViewModel)
    viewModelOf(::MyStoriesViewModel)
    viewModelOf(::TopicDetailViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ReportViewModel)
    viewModelOf(::RulesViewModel)
    viewModelOf(::FullTopicViewModel)
    viewModelOf(::StoriesViewModel)
    viewModelOf(::SavedStoriesViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::StoryDetailViewModel)
    viewModel {
        WritingViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
//    viewModelOf(::WritingViewModel)
    viewModelOf(::SettingsViewModel)
}