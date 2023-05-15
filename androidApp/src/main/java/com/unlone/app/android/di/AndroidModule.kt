package com.unlone.app.android.di

import com.unlone.app.android.viewmodel.EditHistoryViewModel
import com.unlone.app.android.viewmodel.WritingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val androidModule = module {
    viewModelOf(::EditHistoryViewModel)
    viewModelOf(::WritingViewModel)
}