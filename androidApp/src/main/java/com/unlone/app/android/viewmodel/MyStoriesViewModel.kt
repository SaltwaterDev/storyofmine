package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import kotlinx.coroutines.launch


data class MyStoriesUiState(
    val stories: List<SimpleStory> = List(3) { SimpleStory.mock() },
    val loading: Boolean = false,
    val error: String? = null,
)


class MyStoriesViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    var uiState by mutableStateOf(MyStoriesUiState())
        private set

    init {
        loadMyStories()
    }

    private fun loadMyStories() = viewModelScope.launch {
        uiState = uiState.copy(loading = true)
        when (val result = storyRepository.getMyStories()) {
            is StoryResult.Success -> result.data?.let {
                uiState = uiState.copy(stories = it.sortedByDescending { s -> s.createdDate })
            }
            is StoryResult.Failed -> uiState = uiState.copy(error = result.errorMsg)
            is StoryResult.UnknownError -> uiState = uiState.copy(error = result.errorMsg)
        }
        uiState = uiState.copy(loading = false)
    }

    fun dismissError() {
        uiState = uiState.copy(error = null)
    }
}
