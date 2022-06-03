package com.unlone.app.android.di

import com.unlone.app.android.data.repo.DraftRepository
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.viewmodel.PostDetailViewModel
import com.unlone.app.viewmodel.WritingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val androidModule = module {

    single { DraftRepository() }

    viewModel { SignInViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { StoriesViewModel(get()) }
    viewModel { PostDetailViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { WritingViewModel(get()) }
}