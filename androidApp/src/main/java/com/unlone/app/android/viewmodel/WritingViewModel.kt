package com.unlone.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.unlone.app.android.data.repo.DraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class WritingUiState(
    val title: String = "Untitled",
    val content: String = "",
    val draftList: List<String> = emptyList()
)

@HiltViewModel
class WritingViewModel @Inject constructor(
    draftRepository: DraftRepository
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
}