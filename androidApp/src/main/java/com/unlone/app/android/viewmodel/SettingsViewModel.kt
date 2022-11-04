package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import com.unlone.app.data.userPreference.UnloneLocale
import com.unlone.app.data.userPreference.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class SettingUiState(
    val loading: Boolean = true,
    val errorMsg: String? = null,
    val currentLocale: UnloneLocale? = null
)


class SettingsViewModel(
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _state: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState())
    val state = _state.asStateFlow()

    fun refreshData() {
        _state.value = _state.value.copy(loading = true)
        _state.value = _state.value.copy(currentLocale = userPreferenceRepository.getLocale())
        _state.value = _state.value.copy(loading = false)
    }

    fun switchLocaleZh() {
        userPreferenceRepository.setLocale(UnloneLocale.Zh)
    }

    fun switchLocaleEn() {
        userPreferenceRepository.setLocale(UnloneLocale.En)
    }
}