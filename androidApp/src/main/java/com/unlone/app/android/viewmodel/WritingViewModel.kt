package com.unlone.app.android.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.GuidingQuestion
import com.unlone.app.data.write.GuidingQuestionsRepository
import com.unlone.app.data.write.StaticResourceResult
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.domain.useCases.write.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

data class WritingUiState(
    val currentDraftId: String? = null,
    val title: String = "",
    val body: TextFieldValue = TextFieldValue(text = ""),
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
    internal val guidingQuestion: List<GuidingQuestion> = listOf(),
    val displayingGuidingQuestion: GuidingQuestion? = null,
)


class WritingViewModel(
    getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    private val getLastOpenedDraftUseCase: GetLastOpenedDraftUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val queryDraftUseCase: QueryDraftUseCase,
    private val createNewDraftUseCase: CreateNewDraftUseCase,
    private val postStoryUseCase: PostStoryUseCase,
    private val topicRepository: TopicRepository,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val draftRepository: DraftRepository,
    private val guidingQuestionsRepository: GuidingQuestionsRepository,
) : ViewModel() {

    private val changedChannel = Channel<WritingUiState>()
    private val stateChangedResult = changedChannel.receiveAsFlow()

    val state: StateFlow<WritingUiState> = combine(
        stateChangedResult,
        getAllDraftsTitleUseCase(),
        getTopicList(),
        isUserSignedInUseCase(),
        loadGuidingQuestionsFlow(),
    ) { changed, allDraftTitles, topicList, isUserSignedIn, guidingQuestions ->
        changed.copy(
            draftList = allDraftTitles,
            topicList = topicList,
            isUserSignedIn = isUserSignedIn,
            guidingQuestion = guidingQuestions ?: listOf()
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, WritingUiState())

    init {
        viewModelScope.launch {
            refreshData()
        }
    }

    suspend fun refreshData(draftId: String? = null, version: String? = null) =
        withContext(Dispatchers.Default) {
            if (draftId == null || version == null) {
                getLastOpenedDraftUseCase().let { lastOpened ->
                    changedChannel.send(
                        state.value.copy(
                            currentDraftId = lastOpened?.first,
                            title = lastOpened?.second?.title ?: "",
                            body = TextFieldValue(lastOpened?.second?.content ?: ""),
                        )
                    )
                }
            } else {
                queryDraftUseCase(draftId, version).collectLatest {
                    changedChannel.send(
                        state.value.copy(
                            currentDraftId = it.first,
                            title = it.second.title,
                            body = TextFieldValue(it.second.content),
                        )
                    )
                }
            }
        }

    fun dismiss() {
        viewModelScope.launch {
            changedChannel.send(
                state.value.copy(
                    error = null,
                    postSuccess = false
                )
            )
        }
    }


    fun setTitle(title: String) {
        viewModelScope.launch {
            changedChannel.send(state.value.copy(title = title))
        }
    }

    fun setBody(content: String) {
        viewModelScope.launch {
            changedChannel.send(
                state.value.copy(
                    body = state.value.body.copy(
                        text = content,
                        selection = TextRange(content.length)
                    )
                )
            )
        }
    }

    fun saveDraft() = viewModelScope.launch(Dispatchers.Default) {
        Log.d("TAG", "saveDraft: " + state.value.currentDraftId)
        if (state.value.title.isNotBlank()) {
            val result = saveDraftUseCase(
                state.value.currentDraftId,
                state.value.title,
                state.value.body.text
            )
            when (result) {
                is StoryResult.Success -> changedChannel.send(
                    state.value.copy(currentDraftId = result.data)
                )
                is StoryResult.Failed -> changedChannel.send(
                    state.value.copy(error = result.errorMsg)
                )
                else -> {}  // won't hit this case for now
            }
        }
    }

    fun addImageMD(uri: Uri?) {
        uri?.let {
            val imageMD = "![image]($it)"
            setBody(state.value.body.text + imageMD)
        }
    }

    // region option menu feature
    fun clearBody() {
        viewModelScope.launch {
            changedChannel.send(state.value.copy(body = TextFieldValue(text = "")))
        }
    }

    fun createNewDraft() {
        saveDraft()
        viewModelScope.launch {
            val newDraftMap = createNewDraftUseCase()
            changedChannel.send(
                state.value.copy(
                    currentDraftId = newDraftMap["id"],
                    title = newDraftMap["title"] ?: "",
                    body = TextFieldValue(text = newDraftMap["content"] ?: ""),
                    selectedTopic = newDraftMap["selectedTopic"] ?: "",
                )
            )
        }
    }

    fun switchDraft(id: String) {
        if (id == state.value.currentDraftId) return

        viewModelScope.launch {
            saveDraft().join()
            queryDraftUseCase(id).collect {
                changedChannel.send(
                    state.value.copy(
                        currentDraftId = it.first,
                        title = it.second.title,
                        body = TextFieldValue(text = it.second.content),
                    )
                )
            }
        }
    }

    fun deleteDraft(id: String) {
        viewModelScope.launch {
            draftRepository.deleteDraft(id)
            // remove current content if deleting the current one
            if (id == state.value.currentDraftId) {
                changedChannel.send(
                    state.value.copy(title = "", body = TextFieldValue(""))
                )
            }
        }
    }

    // endregion

    // region set posting config
    fun setPublished(isPublished: Boolean) {
        viewModelScope.launch {
            changedChannel.send(
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
            changedChannel.send(
                state.value.copy(
                    commentAllowed = commentAllowed
                )
            )
        }
    }

    fun setSaveAllowed(saveAllowed: Boolean) {
        viewModelScope.launch {
            changedChannel.send(
                state.value.copy(
                    saveAllowed = saveAllowed
                )
            )
        }
    }

    fun postStory() = viewModelScope.launch {
        changedChannel.send(
            state.value.copy(loading = true)
        )
        val result = postStoryUseCase(
            state.value.title,
            state.value.body.text,
            state.value.selectedTopic,
            state.value.isPublished,
            state.value.commentAllowed,
            state.value.saveAllowed,
        )
        changedChannel.send(
            when (result) {
                is StoryResult.Success -> {
                    state.value.currentDraftId?.let { deleteDraft(it) } ?: createNewDraft()
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
                is StoryResult.UnknownError ->
                    state.value.copy(
                        error = result.errorMsg,
                        loading = false,
                    )
            }
        )
        Log.d("TAG", "postStory: $result")
    }

    fun setTopic(topic: String) {
        viewModelScope.launch {
            changedChannel.send(
                state.value.copy(
                    selectedTopic = topic
                )
            )
        }
    }

    private fun getTopicList(): Flow<List<String>> {
        return flow {
            emit(topicRepository.getAllTopic().map { topic -> topic.name })
        }
    }
    // endregion

    // region guiding question
    private fun loadGuidingQuestionsFlow() = flow {
        when (val result = guidingQuestionsRepository.getGuidingQuestionList()) {
            is StaticResourceResult.Success -> emit(result.data)
            is StaticResourceResult.Failed -> {
                changedChannel.send(
                    state.value.copy(error = result.errorMsg)
                )
            }
            is StaticResourceResult.UnknownError -> {
                changedChannel.send(
                    state.value.copy(error = result.errorMsg)
                )
            }
        }
    }


    private var guidingQuestionIterator = state.value.guidingQuestion.listIterator()
    private var dismissQuestionJob: Job? = null
    suspend fun getDisplayingQuestion() {
        if (!guidingQuestionIterator.hasNext()) {
            // reset the iterator
            guidingQuestionIterator = state.value.guidingQuestion.listIterator()
        }

        dismissQuestionJob?.cancelAndJoin()
        dismissQuestionJob = viewModelScope.launch {
            delay(10.seconds)
            changedChannel.send(
                state.value.copy(displayingGuidingQuestion = null)
            )
        }

        if (guidingQuestionIterator.hasNext())
            changedChannel.send(
                state.value.copy(displayingGuidingQuestion = guidingQuestionIterator.next())
            )
    }
    // endregion
}