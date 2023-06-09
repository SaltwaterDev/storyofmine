package com.example.app.android.ui.write

import androidx.compose.runtime.Stable
import com.example.app.data.story.PublishStoryException
import com.example.app.data.write.GuidingQuestion

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
    val isTitleAndBodyEmpty: Boolean
    val guidingQuestion: List<GuidingQuestion>
    val displayingGuidingQuestion: GuidingQuestion?
    val postSucceedStory: String?
    val shouldCreateNewVersionDraft: Boolean
}

