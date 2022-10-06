package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.comonComponent.TopicDetailTopBar
import com.unlone.app.android.viewmodel.TopicDetailViewModel


@Composable
fun TopicDetail(
    topic: String?,
    back: () -> Unit,
    navToStoryDetail: (String) -> Unit,
    viewModel: TopicDetailViewModel
) {
    val uiState = viewModel.state.value
    LaunchedEffect(Unit) {
        topic?.let { viewModel.initData(it) }
    }

    Scaffold(modifier = Modifier
        .displayCutoutPadding()
        .statusBarsPadding(), topBar = {
        TopicDetailTopBar(
            back,
            uiState.topic ?: "",
            true,
            viewModel::toggleFollowing,
        )
    }) {
        SwipeRefresh(state = rememberSwipeRefreshState(uiState.loading),
            onRefresh = { topic?.let { it1 -> viewModel.initData(it1) } }) {

            LazyColumn {
                uiState.stories?.let {
                    items(it) { story ->
                        StoryCard(
                            story.title,
                            story.content,
                            uiState.loading,
                            Modifier
                                .padding(16.dp)
                                .fillParentMaxWidth()
                                .placeholder(
                                    visible = uiState.loading,
                                    highlight = PlaceholderHighlight.fade()
                                )
                        ) { navToStoryDetail(story.id) }
                    }
                }
            }

        }
    }
}