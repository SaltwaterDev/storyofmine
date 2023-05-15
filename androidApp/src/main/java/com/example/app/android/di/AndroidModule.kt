package com.example.app.android.di

import com.example.app.android.viewmodel.EditHistoryViewModel
import com.example.app.android.viewmodel.WritingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val androidModule = module {
    viewModelOf(::EditHistoryViewModel)
    viewModelOf(::WritingViewModel)
}