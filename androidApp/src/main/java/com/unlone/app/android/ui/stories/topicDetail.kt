package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.comonComponent.TopicDetailTopBar
import com.unlone.app.android.ui.connectivityState
import com.unlone.app.android.viewmodel.TopicDetailViewModel
import com.unlone.app.domain.entities.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TopicDetail(
    topic: String?,
    back: () -> Unit,
    navToStoryDetail: (String) -> Unit,
    viewModel: TopicDetailViewModel,
) {
    val uiState = viewModel.state.collectAsState().value
    val coroutineScope = rememberCoroutineScope()


    val networkState by connectivityState()
    if (networkState is NetworkState.Available) {
        LaunchedEffect(networkState) {
            topic?.let { viewModel.initData(it) }
        }
    }

    Scaffold(
        modifier = Modifier
            .displayCutoutPadding()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopicDetailTopBar(
                back,
                uiState.topic ?: "",
                true,
                viewModel::toggleFollowing,
            )
        }) { padding ->
        SwipeRefresh(state = rememberSwipeRefreshState(uiState.isRefreshing),
            onRefresh = { topic?.let { it1 -> coroutineScope.launch { viewModel.refresh(it1) } } }) {

            LazyColumn {
                uiState.stories?.let {
                    items(it) { story ->
                        StoryCard(
                            story.title,
                            story.content,
                            uiState.loading,
                            Modifier
                                .fillParentMaxWidth()
                                .padding(16.dp)
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