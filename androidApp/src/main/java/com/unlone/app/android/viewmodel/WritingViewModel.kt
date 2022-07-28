package com.unlone.app.android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.domain.useCases.write.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WritingUiState(
    val currentDraftId: String? = null,
    val title: String = "",
    val content: String = "",
    val draftList: Map<String, String> = mapOf(),
    val topicList: List<String> = listOf(),
    val selectedTopic: String = "",
    val isPublished: Boolean = false,
    val commentAllowed: Boolean = false,
    val saveAllowed: Boolean = false,
    val error: String? = null,
    val postSuccess: Boolean = false,
    val loading: Boolean = false,
    val isUserSignedIn: Boolean = false,
)


class WritingViewModel(
    getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    getLastOpenedDraftUseCase: GetLastOpenedDraftUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val queryDraftUseCase: QueryDraftUseCase,
    private val createNewDraftUseCase: CreateNewDraftUseCase,
    private val postStoryUseCase: PostStoryUseCase,
    private val topicRepository: TopicRepository,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
) : ViewModel() {

    private val stateChangedChannel = Channel<WritingUiState>()
    private val stateChangedResult = stateChangedChannel.receiveAsFlow()

    val state: StateFlow<WritingUiState> = combine(
        stateChangedResult,
        getAllDraftsTitleUseCase(),
    ) { changed, allDraftTitles ->
        changed.copy(
            draftList = allDraftTitles
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, WritingUiState())

    init {
        viewModelScope.launch {
            getLastOpenedDraftUseCase()
                .filterNotNull()
                .first()
                .let { lastOpened ->
                stateChangedChannel.send(
                    WritingUiState(
                        currentDraftId = lastOpened.first,
                        title = lastOpened.second.title,
                        content = lastOpened.second.content,
                        topicList = topicRepository.getAllTopic().map { topic -> topic.name },
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

    fun saveDraft() = viewModelScope.launch {
        saveDraftUseCase(
            state.value.currentDraftId,
            state.value.title,
            state.value.content
        )
    }

    fun createNewDraft() {
        viewModelScope.launch {
            saveDraft().join()
            val newDraftMap = createNewDraftUseCase()
            stateChangedChannel.send(
                state.value.copy(
                    currentDraftId = newDraftMap["id"],
                    title = newDraftMap["title"] ?: "",
                    content = newDraftMap["content"] ?: "",
                    selectedTopic = newDraftMap["selectedTopic"] ?: "",
                )
            )
        }
    }

    fun switchDraft(id: String) {
        viewModelScope.launch {
            saveDraft().join()
            queryDraftUseCase(id).collect {
                stateChangedChannel.send(
                    state.value.copy(
                        currentDraftId = it.first,
                        title = it.second.title,
                        content = it.second.content,
                    )
                )
            }
        }
    }

    fun setPublished(isPublished: Boolean) {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(
                    isPublished = isPublished,
                    commentAllowed = state.value.commentAllowed && isPublished,
                    saveAllowed = state.value.saveAllowed && isPublished,
                )
            )
        }
    }

    fun setCommentAllowed(commentAllowed: Boolean) {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(
                    commentAllowed = commentAllowed
                )
            )
        }
    }

    fun setSaveAllowed(saveAllowed: Boolean) {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(
                    saveAllowed = saveAllowed
                )
            )
        }
    }

    fun postStory() {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(loading = true)
            )
            val result = postStoryUseCase(
                state.value.title,
                state.value.content,
                state.value.selectedTopic,
                state.value.isPublished,
                state.value.commentAllowed,
                state.value.saveAllowed,
            )
            stateChangedChannel.send(
                when (result) {
                    is StoryResult.Success -> {
                        createNewDraft()
                        state.value.copy(
                            postSuccess = true,
                            loading = false,
                        )
                    }
                    is StoryResult.Failed ->
                        state.value.copy(
                            error = result.errorMsg,
                            loading = false,
                        )
                }
            )
            Log.d("TAG", "postStory: $result")
        }
    }

    fun setTopic(topic: String) {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(
                    selectedTopic = topic
                )
            )
        }
    }

    fun dismiss() {
        viewModelScope.launch {
            stateChangedChannel.send(
                state.value.copy(
                    error = null,
                    postSuccess = false
                )
            )
        }
    }

    suspend fun getIsUserSignedIn() = isUserSignedInUseCase()

}