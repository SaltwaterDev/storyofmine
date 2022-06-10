package com.unlone.app.android.di

import com.unlone.app.android.data.repo.DraftRepository
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.auth.ValidPasswordUseCase
import com.unlone.app.viewmodel.PostDetailViewModel
import com.unlone.app.viewmodel.WritingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val androidModule = module {

    single { DraftRepository() }

    viewModel { SignInViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { StoriesViewModel(get()) }
    viewModel { PostDetailViewModel() }
    viewModel { ProfileViewModel(get()) }
    viewModel { WritingViewModel(get()) }
}