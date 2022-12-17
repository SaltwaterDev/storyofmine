package com.unlone.app.android.ui.write

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.data.write.GuidingQuestion

@Stable
interface WritingUiState {
    val currentDraftId: String?
    val title: String
    val body: String
    val draftList: Map<String, String>
    val topicList: List<String>
    val selectedTopic: String
    val isPublished: Boolean
    val commentAllowed: Boolean
    val saveAllowed: Boolean
    val error: String?
    val postSuccess: Boolean
    val postStoryError: PublishStoryException?
    val storyPosting: Boolean
    val loading: Boolean
    val isUserSignedIn: Boolean
    val guidingQuestion: List<GuidingQuestion>
    val displayingGuidingQuestion: GuidingQuestion?
    val postSucceedStory: String?
    val shouldCreateNewVersionDraft: Boolean
}

