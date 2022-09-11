package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unlone.app.android.ui.comonComponent.TopicDetailTopBar


@Composable
fun TopicDetail(
    topicId: String,
    back: () -> Unit,
    navToStoryDetail: () -> Unit,
    // vm: TopicDetailViewModel = viewModel(),
) {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
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