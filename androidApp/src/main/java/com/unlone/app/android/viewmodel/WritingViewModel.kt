package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.write.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init

data class WritingUiState(
    val currentDraftId: String? = null,
    val title: String = "",
    val content: String = "",
    val draftList: Map<String, String> = mapOf()
)


class WritingViewModel(
    getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    getLastEditedDraftUseCase: GetLastEditedDraftUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
) : ViewModel() {

    private val stateChangedChannel = Channel<WritingUiState>()
    private val stateChangedResult = stateChangedChannel.receiveAsFlow()

    //    private val _state = MutableStateFlow(WritingUiState())
    val state: StateFlow<WritingUiState> = combine(
        stateChangedResult,
        getAllDraftsTitleUseCase()
    ) { changed, allDraftTitles ->
        changed.copy(
            draftList = allDraftTitles
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, WritingUiState())

    val draftList: StateFlow<Map<String, String>> =
        getAllDraftsTitleUseCase().stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    init {
        viewModelScope.launch {
            getLastEditedDraftUseCase().filterNotNull().collect {
                stateChangedChannel.send(
                    WritingUiState(
                        currentDraftId = it.first,
                        title = it.second.title,
                        content = it.second.content,
                    )
                )
            }
        }
    }


    fun setTitle(title: String) {
        viewModelScope.launch {
            stateChangedChannel.send(state.value.copy(title = title))
        }
    }

    fun setContent(content: String) {
        viewModelScope.launch {
            stateChangedChannel.send(state.value.copy(content = content))
        }
    }

    fun clearTitleAndContent() {
        viewModelScope.launch {
            stateChangedChannel.send(state.value.copy(title = "", content = ""))
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            if (state.value.title.isNotBlank() && state.value.content.isNotBlank()) {
                saveDraftUseCase(
                    state.value.currentDraftId,
                    state.value.title,
                    state.value.content
                )
            }
        }
    }
}