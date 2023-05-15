package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.ui.write.WritingUiState
import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.data.write.GuidingQuestion
import com.unlone.app.domain.useCases.write.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber

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
    override val isTitleAndBodyEmpty: Boolean
        get() = title.isEmpty() || body.isEmpty()
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
    private val createNewDraftUseCase: CreateNewDraftUseCase,
    private val getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase,
    private val getLastOpenedDraftUseCase: GetLastOpenedDraftUseCase,
    private val queryDraftUseCase: QueryDraftUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val draftRepository: DraftRepository,
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

    private fun shouldNowSaveToLatestVersion() {
        _uiState.shouldCreateNewVersionDraft = false
    }


    fun setTitle(text: String) {
        _uiState.title = text
        saveDraft()
    }

    val setBody: (String) -> Unit = { text: String ->
        _uiState.body = text
        saveDraft()
    }

    private fun saveDraft() = viewModelScope.launch {
        if (_uiState.title.isBlank() && _uiState.body.isBlank()) return@launch

        Timber.d("currentDraftId ${uiState.currentDraftId}")
        when (val result =
            saveDraftUseCase(
                _uiState.currentDraftId,
                _uiState.title,
                _uiState.body,
                _uiState.shouldCreateNewVersionDraft
            )) {
            is StoryResult.Success -> {
                _uiState.currentDraftId = result.data?.first
            }
            is StoryResult.Failed -> {
                _uiState.error = result.errorMsg
            }
            else -> { /*won't hit this case for now*/ }
        }
        if (_uiState.shouldCreateNewVersionDraft) {
            shouldNowSaveToLatestVersion()
        }
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
        resetShouldCreateNewVersionDraft()
    }


    suspend fun switchDraft(id: String) {
        _uiState.loading = true
        saveDraft().join()

        val result = queryDraftUseCase(id).first()
        _uiState.loading = false
        _uiState.currentDraftId = result.first
        _uiState.title = result.second.title
        _uiState.body = result.second.content
        resetShouldCreateNewVersionDraft()
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



    fun cleanUpState() {
        _uiState.postSuccess = false
    }

}
