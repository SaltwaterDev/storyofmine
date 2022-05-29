package com.unlone.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.data.repo.AuthRepository
import com.unlone.app.model.PostsByTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class LoungeUiState(
    val isUserLoggedIn: Boolean = true,
    val postsByTopics: List<PostsByTopic>? = null
)


@HiltViewModel
class LoungeViewModel @Inject constructor(
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val state = authRepository.isUserLoggedIn.map {
        LoungeUiState(
            isUserLoggedIn = it,
            postsByTopics = if (it) getPosts() else null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        LoungeUiState()
    )
}

fun getPosts(): List<PostsByTopic> {
    return listOf(
        PostsByTopic.mock(),
        PostsByTopic.mock(),
        PostsByTopic.mock(),
        PostsByTopic.mock()
    )
}
