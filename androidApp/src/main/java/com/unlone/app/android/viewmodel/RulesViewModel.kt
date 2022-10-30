package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.rules.RulesRepository
import com.unlone.app.data.write.StaticResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class RulesUiState(
    val loading: Boolean = false,
    val rules: List<String> = listOf(),
    val error: String? = null
)

class RulesViewModel(
    private val rulesRepository: RulesRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(RulesUiState())

    init {
        getRules()
    }


    private fun getRules() = viewModelScope.launch(Dispatchers.Default) {
        uiState.value = uiState.value.copy(loading = true)
        when (val result = rulesRepository.getRules()) {
            is StaticResourceResult.Success -> {
                result.data?.let {
                    uiState.value = uiState.value.copy(rules = it.map { it1 -> it1.text })
                }
            }
            is StaticResourceResult.Failed -> {
                uiState.value = uiState.value.copy(error = result.errorMsg)
            }
            is StaticResourceResult.UnknownError -> {
                uiState.value = uiState.value.copy(error = result.errorMsg)
            }
        }
        uiState.value = uiState.value.copy(loading = false)
    }

}