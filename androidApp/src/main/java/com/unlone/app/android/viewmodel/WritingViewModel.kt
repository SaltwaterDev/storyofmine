package com.unlone.app.android.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.ui.navigation.optionalDraftArg
import com.unlone.app.android.ui.navigation.optionalVersionArg
import com.unlone.app.android.ui.write.WritingUiState
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.GuidingQuestion
import com.unlone.app.data.write.GuidingQuestionsRepository
import com.unlone.app.data.write.StaticResourceResult
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.domain.useCases.write.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

private class MutableWritingUiState : WritingUiState {
    override var body: TextFieldValue by mutableStateOf(TextFieldValue(text = ""))
    override var commentAllowed: Boolean by mutableStateOf(false)
    override var currentDraftId: String? by mutableStateOf<String?>(null)
    override var displayingGuidingQuestion: GuidingQuestion? by mutableStateOf(null)
    override var draftList: Map<String, String> by mutableStateOf(mapOf())
    override var error: String? by mutableStateOf(null)
    override var guidingQuestion: List<GuidingQuestion> by mutableStateOf(listOf())
    override var isPublished: Boolean by mutableStateOf(false)
    override var isUserSignedIn: Boolean by mutableStateOf(false)
    override var loading: Boolean by mutableStateOf(false)
    override var postSuccess: Boolean by mutableStateOf(false)
    override var postSucceedStory: String? by mutableStateOf(null)
    override var title: String by mutableStateOf("")
    override var topicList: List<String> by mutableStateOf(listOf())
    override var saveAllowed: Boolean by mutableStateOf(false)
    override var selectedTopic: String by mutableStateOf("")
    override var storyPosting: Boolean by mutableStateOf(false)
}

class WritingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    private val getLastOpenedDraftUseCase: GetLastOpenedDraftUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val queryDraftUseCase: QueryDraftUseCase,
    private val createNewDraftUseCase: CreateNewDraftUseCase,
    private val postStoryUseCase: PostStoryUseCase,
    private val topicRepository: TopicRepository,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val draftRepository: DraftRepository,
    private val guidingQuestionsRepository: GuidingQuestionsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableWritingUiState()
    val uiState: WritingUiState = _uiState

    init {
        viewModelScope.launch { refreshData() }
    }

    suspend fun refreshData(networkAvailable: Boolean = true) = withContext(Dispatchers.Main) {

        val draftId = savedStateHandle.get<String>(optionalDraftArg)
        val version = savedStateHandle.get<String>(optionalVersionArg)

        _uiState.loading = true
        _uiState.guidingQuestion = listOf()
        guidingQuestionIterator = null

        if (networkAvailable) {
            launch { getTopicList() }
            launch {
                isUserSignedInUseCase().catch { e ->
                    _uiState.error = e.message
                }.collect {
                    _uiState.isUserSignedIn = it
                }
            }
            launch {
                _uiState.guidingQuestion = loadGuidingQuestions()
            }
        }

        launch {
            getAllDraftsTitleUseCase().catch { e ->
                _uiState.error = e.message
            }.collect {
                _uiState.draftList = it
            }
        }

        launch {
            if (draftId.isNullOrBlank() || version.isNullOrBlank()) {
                getLastOpenedDraftUseCase().let { lastOpened ->
                    _uiState.currentDraftId = lastOpened?.first
                    _uiState.title = lastOpened?.second?.title ?: ""
                    _uiState.body = TextFieldValue(lastOpened?.second?.content ?: "")
                }
            } else {
                queryDraftUseCase(draftId, version).collectLatest {
                    _uiState.currentDraftId = it.first
                    _uiState.title = it.second.title
                    _uiState.body = TextFieldValue(it.second.content)
                }
            }
            _uiState.loading = false
        }
    }

    fun dismiss() {
        _uiState.error = null
    }


    fun setTitle(title: String) {
        _uiState.title = title
    }

    fun setBody(text: String) {
        _uiState.body = _uiState.body.copy(
            text = text,
            selection = TextRange(text.length)
        )
    }

    fun saveDraft() = viewModelScope.launch(Dispatchers.Default) {
        if (uiState.title.isNotBlank() || uiState.body.text.isNotBlank()) {
            Timber.d(uiState.currentDraftId)
            val result = saveDraftUseCase(
                uiState.currentDraftId, uiState.title, uiState.body.text
            )
            when (result) {
                is StoryResult.Success -> _uiState.currentDraftId = result.data
                is StoryResult.Failed -> _uiState.error = result.errorMsg
                else -> {}  // won't hit this case for now
            }
        }
    }

    fun addImageMD(uri: Uri?) {
        uri?.let {
            val imageMD = "![image]($it)"
            setBody(uiState.body.text + imageMD)
        }
    }

    fun dismissSucceed() {
        _uiState.postSuccess = false
    }

    // region option menu feature
    fun clearDraft() {
        _uiState.title = ""
        _uiState.body = TextFieldValue(text = "")
    }

    fun createNewDraft() = viewModelScope.launch {
        saveDraft().join()

        val newDraftMap = createNewDraftUseCase()
        _uiState.currentDraftId = newDraftMap["id"]
        _uiState.title = newDraftMap["title"] ?: ""
        _uiState.body = TextFieldValue(text = newDraftMap["content"] ?: "")
        _uiState.selectedTopic = newDraftMap["selectedTopic"] ?: ""
    }


    suspend fun switchDraft(id: String) {
        _uiState.loading = true
        saveDraft().join()

        val result = queryDraftUseCase(id).first()
        _uiState.loading = false
        _uiState.currentDraftId = result.first
        _uiState.title = result.second.title
        _uiState.body = TextFieldValue(text = result.second.content)
    }


    suspend fun deleteDraft(id: String) {
        draftRepository.deleteDraft(id)
        // remove current content if deleting the current one
        if (id == uiState.currentDraftId) {
            _uiState.currentDraftId = null
            clearDraft()
        }
    }

    // endregion

    // region set posting config
    fun setPublished(isPublished: Boolean) {
        _uiState.isPublished = isPublished
        _uiState.commentAllowed = _uiState.commentAllowed && isPublished
        _uiState.saveAllowed = _uiState.saveAllowed && isPublished
    }


    fun setCommentAllowed(commentAllowed: Boolean) {
        _uiState.commentAllowed = commentAllowed
    }

    fun setSaveAllowed(saveAllowed: Boolean) {
        _uiState.saveAllowed = saveAllowed
    }

    fun postStory() = viewModelScope.launch {
        _uiState.storyPosting = true
        val result = postStoryUseCase(
            uiState.title,
            uiState.body.text,
            uiState.selectedTopic,
            uiState.isPublished,
            uiState.commentAllowed,
            uiState.saveAllowed,
        )
        when (result) {
            is StoryResult.Success -> {
                uiState.currentDraftId?.let { deleteDraft(it) } ?: createNewDraft()
                _uiState.postSucceedStory = result.data
                _uiState.postSuccess = true
            }
            is StoryResult.Failed -> _uiState.error = result.errorMsg
            is StoryResult.UnknownError -> _uiState.error = result.errorMsg
        }
        _uiState.storyPosting = false
        Timber.d(result.data)
    }

    fun setTopic(topic: String) {
        _uiState.selectedTopic = topic
    }

    private suspend fun getTopicList() {
        when (val result = topicRepository.getAllTopic()) {
            is StoryResult.Success -> {
                result.data?.map { topic -> topic.name }?.let {
                    _uiState.topicList = it
                }
            }
            is StoryResult.Failed -> {
                /*todo*/
            }
            is StoryResult.UnknownError -> {
                /*todo*/
            }
        }
    }
    // endregion

    // region guiding question
    private suspend fun loadGuidingQuestions(): List<GuidingQuestion> {
        return when (val result = guidingQuestionsRepository.getGuidingQuestionList()) {
            is StaticResourceResult.Success -> result.data ?: listOf()
            is StaticResourceResult.Failed -> {
                _uiState.error = result.errorMsg
                listOf()
            }
            is StaticResourceResult.UnknownError -> {
                listOf()
            }
        }
    }


    private var guidingQuestionIterator: ListIterator<GuidingQuestion>? =
        uiState.guidingQuestion.listIterator()
    private var dismissQuestionJob: Job? = null
    suspend fun getDisplayingQuestion() {
        if (guidingQuestionIterator?.hasNext() != true) {
            // reset the iterator
            guidingQuestionIterator = uiState.guidingQuestion.listIterator()
        }

        dismissQuestionJob?.cancelAndJoin()
        dismissQuestionJob = viewModelScope.launch {
            delay(6.seconds)
            _uiState.displayingGuidingQuestion = null
        }

        if (guidingQuestionIterator?.hasNext() == true) {
            _uiState.displayingGuidingQuestion = guidingQuestionIterator!!.next()
        }
    }
    // endregion

    suspend fun checkAuthentication() {
        authRepository.authenticate()
    }

}
