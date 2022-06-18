package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.write.DraftRepository
import kotlinx.coroutines.launch

data class WritingUiState(
    val title: String = "Untitled",
    val content: String = "",
    val draftList: List<String> = emptyList()
)


class WritingViewModel(
    val draftRepository: DraftRepository
) : ViewModel() {

    var state by mutableStateOf(
        WritingUiState(
            draftList = draftRepository.getAllDraftTitles()
        )
    )
        private set

    fun setTitle(title: String) {
        state = state.copy(title = title)
    }

    fun setContent(content: String) {
        state = state.copy(content = content)
    }

    fun clearTitleAndContent() {
        state = state.copy(title = "", content = "")
    }

    fun saveDraft(){
        viewModelScope.launch {
            draftRepository.saveDraft(state.title, state.content)
        }
    }
}