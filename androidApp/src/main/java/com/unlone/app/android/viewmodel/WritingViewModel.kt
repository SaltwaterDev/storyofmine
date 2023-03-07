package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.ui.write.WritingUiState
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.GuidingQuestion
import com.unlone.app.data.write.GuidingQuestionsRepository
import com.unlone.app.data.write.StaticResourceResult
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.domain.useCases.write.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

private class MutableWritingUiState : WritingUiState {
    override var body: String by mutableStateOf("")
    override var commentAllowed: Boolean by mutableStateOf(false)
    override var currentDraftId: String? by mutableStateOf(null)
    override var displayingGuidingQuestion: GuidingQuestion? by mutableStateOf(null)
    override var draftList: Map<String, String> by mutableStateOf(mapOf())
    override var postStoryError: PublishStoryException? by mutableStateOf(null)
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
    override var shouldCreateNewVersionDraft: Boolean by mutableStateOf(true)
}

class WritingViewModel(
    private val authRepository: AuthRepository,
    private val createNewDraftUseCase: CreateNewDraftUseCase,
    private val getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    private val getLastOpenedDraftUseCase: GetLastOpenedDraftUseCase,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val queryDraftUseCase: QueryDraftUseCase,
    private val postStoryUseCase: PostStoryUseCase,
    private val updateLatestDraftUseCase: UpdateLatestDraftUseCase,
    private val topicRepository: TopicRepository,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val draftRepository: DraftRepository,
    private val guidingQuestionsRepository: GuidingQuestionsRepository,
) : ViewModel() {

    private val _uiState = MutableWritingUiState()
    val uiState: WritingUiState = _uiState

    init {
        viewModelScope.launch { refreshData() }
    }

    suspend fun refreshData(
        networkAvailable: Boolean = false,
        draftIdArg: String? = null,
        versionArg: String? = null,
    ) = withContext(Dispatchers.Main) {

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
            try {
                getAllDraftsTitleUseCase().catch { e ->
                    _uiState.error = e.message
                }.collect {
                    _uiState.draftList = it
                }
            } catch (_: Throwable) {
                // do nothing
            }
        }

        launch {
            if (draftIdArg.isNullOrBlank() || versionArg.isNullOrBlank()) {
                getLastOpenedDraftUseCase().let { lastOpened ->
                    _uiState.currentDraftId = lastOpened?.first
                    _uiState.title = lastOpened?.second?.title ?: ""
                    _uiState.body = lastOpened?.second?.content ?: ""
                }
            } else {
                queryDraftUseCase(draftIdArg, versionArg).first {
                    _uiState.currentDraftId = it.first
                    _uiState.title = it.second.title
                    _uiState.body = it.second.content
                    true
                }
            }
            _uiState.shouldCreateNewVersionDraft = true
            _uiState.loading = false
        }
    }

    fun dismiss() {
        _uiState.error = null
        _uiState.postStoryError = null
    }

    fun resetShouldCreateNewVersionDraft() {
        dismiss()
        _uiState.shouldCreateNewVersionDraft = true
    }


    fun setTitle(text: String) {
        _uiState.title = text
        // real-time save
        saveDraft()
    }

    val setBody: (String) -> Unit = { text: String ->
        // real-time save
        saveDraft(bodyText = text)
    }

    private fun saveDraft(bodyText: String = "") = viewModelScope.launch {
        if (_uiState.title.isBlank() && bodyText.isBlank()) return@launch
        Timber.d("currentDraftId", uiState.currentDraftId)
        if (_uiState.shouldCreateNewVersionDraft && saveAsNewVersionDraft(bodyText)) {
            _uiState.shouldCreateNewVersionDraft = false
        } else {
            saveToLatestVersion(bodyText)
        }
    }

    private suspend fun saveAsNewVersionDraft(bodyText: String): Boolean {
        val result = saveDraftUseCase(
            _uiState.currentDraftId,
            _uiState.title,
            bodyText,
        )
        when (result) {
            is StoryResult.Success -> {
                _uiState.currentDraftId = result.data?.first
                return true

            }
            is StoryResult.Failed -> _uiState.error = result.errorMsg
            else -> {}  // won't hit this case for now
        }
        return false
    }

    private suspend fun saveToLatestVersion(bodyText: String) {
        _uiState.currentDraftId?.let {
            updateLatestDraftUseCase(
                it,
                _uiState.title,
                bodyText,
            )
        }
    }


    fun dismissSucceed() {
        _uiState.postSuccess = false
    }

    // region option menu feature
    fun clearDraft() {
        _uiState.title = ""
        _uiState.body = ""
    }

    fun createNewDraft() = viewModelScope.launch {
        saveDraft().join()

        val newDraftMap = createNewDraftUseCase()
        _uiState.currentDraftId = newDraftMap["id"]
        _uiState.title = newDraftMap["title"] ?: ""
        _uiState.body = newDraftMap["content"] ?: ""
        _uiState.selectedTopic = newDraftMap["selectedTopic"] ?: ""
        _uiState.shouldCreateNewVersionDraft = true
    }


    suspend fun switchDraft(id: String) {
        _uiState.loading = true
        saveDraft().join()

        val result = queryDraftUseCase(id).first()
        _uiState.loading = false
        _uiState.currentDraftId = result.first
        _uiState.title = result.second.title
        _uiState.body = result.second.content
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
        Timber.d("${uiState.title} ${uiState.body}")
        val result = postStoryUseCase(
            uiState.title,
            uiState.body,
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
            is StoryResult.Failed -> {
                result.exception?.let { ex ->
                    if (ex is PublishStoryException) {
                        _uiState.postStoryError = ex
                    }
                }
            }
            is StoryResult.UnknownError -> _uiState.error = result.errorMsg
        }
        _uiState.storyPosting = false
        Timber.d(result.data)
    }

    val setTopic = { topic: String -> _uiState.selectedTopic = topic }

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
