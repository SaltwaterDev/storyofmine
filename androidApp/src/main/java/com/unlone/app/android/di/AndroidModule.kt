package com.unlone.app.android.di

import com.unlone.app.android.viewmodel.*
import com.unlone.app.viewmodel.PostDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val androidModule = module {

    viewModel { SignInViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { StoriesViewModel(get()) }
    viewModel { PostDetailViewModel() }
    viewModel { ProfileViewModel(get()) }
    viewModel { WritingViewModel(get(), get(), get(), get(), get()) }
}