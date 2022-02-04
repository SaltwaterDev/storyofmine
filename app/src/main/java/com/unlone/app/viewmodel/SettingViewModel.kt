package com.unlone.app.viewmodel

import androidx.lifecycle.ViewModel
import com.unlone.app.data.AppConfigRepository
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
): ViewModel()