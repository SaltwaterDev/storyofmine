package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.write.DraftRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WritingUiState(
    val title: String = "",
    val content: String = "",
    val draftList: List<String> = emptyList()
)


class WritingViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WritingUiState())
    val state: StateFlow<WritingUiState> =
        _state.combine(draftRepository.getAllDraftTitles()) { state, draftTitles ->
            state.copy(draftList = draftTitles)
        }.stateIn(viewModelScope, SharingStarted.Lazily, WritingUiState())

    fun setTitle(title: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(title = title)
        }
    }

    fun setContent(content: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(content = content)
        }
    }

    fun clearTitleAndContent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(title = "", content = "")
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            viewModelScope.launch {
                if (_state.value.title.isNotBlank() && _state.value.content.isNotBlank())
                    draftRepository.saveDraft(_state.value.title, _state.value.content)
            }
        }
    }
}