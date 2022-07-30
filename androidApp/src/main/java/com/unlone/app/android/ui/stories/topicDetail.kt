package com.unlone.app.android.ui.stories

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.unlone.app.android.ui.comonComponent.TopicDetailTopBar


@Composable
fun TopicDetail(
    topicId: String,
    back: () -> Unit,
    navToStoryDetail: () -> Unit,
    // vm: TopicDetailViewModel = viewModel(),
) {
    Scaffold(
        topBar = {
            TopicDetailTopBar(
                back,
                topicId,
                true,
                { /*todo*/ },
            )
        }
    ) {
        LazyColumn{

        }
    }
}