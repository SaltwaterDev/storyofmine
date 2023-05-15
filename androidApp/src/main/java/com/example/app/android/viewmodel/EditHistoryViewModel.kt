package com.example.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.entities.DraftVersion
import com.example.app.domain.useCases.write.GetDraftAllVersionsUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class EditHistoryUiState(
    val drafts: List<DraftVersion> = listOf(),
    val loading: Boolean = false
)


class EditHistoryViewModel(
    private val getDraftAllVersionsUseCase: GetDraftAllVersionsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(EditHistoryUiState())
        private set

    fun loadDraftVersions(draftId: String) = viewModelScope.launch {
        uiState = uiState.copy(loading = true)
        getDraftAllVersionsUseCase(draftId).collectLatest { d ->
            uiState = uiState.copy(drafts = d.second)
            uiState = uiState.copy(loading = false)
        }
    }
}