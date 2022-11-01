package com.unlone.app.android.ui.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.theme.MontserratFontFamily
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.data.story.SimpleStory
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items

@Composable
fun StoriesScreen(
    viewModel: StoriesViewModel,
    navToPostDetail: (String) -> Unit = {},
    navToTopicPosts: (String) -> Unit = {},
    navToSignIn: () -> Unit = {},
    navToSignUp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val storiesByTopics = viewModel.storiesByTopics.collectAsLazyPagingItems()
    val refreshState =
        rememberSwipeRefreshState(storiesByTopics.loadState.refresh is LoadState.Loading)

    LaunchedEffect(state.isUserLoggedIn) {
        viewModel.initData()
    }


    if (state.isUserLoggedIn) {
        viewModel.checkAuth()   // to ensure again user has authorized
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SwipeRefresh(
                state = refreshState,
                onRefresh = {
                    storiesByTopics.refresh()
                }
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = stringResource(
                                resource = SharedRes.strings.stories_header_greeting,
                                state.username ?: ""
                            ),
                            modifier = Modifier
                                .padding(16.dp, 40.dp)
                                .placeholder(
                                    visible = state.loading,
                                    highlight = PlaceholderHighlight.fade()
                                ),
                            fontSize = 30.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(storiesByTopics, key = { it.topic }) {
                        PostsByTopic(
                            it!!.topic,
                            state.loading,
                            it.stories,
                            { navToTopicPosts(it.topic) }
                        ) { pid ->
                            navToPostDetail(pid)
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }

        state.errorMsg?.let {
            AlertDialog(
                onDismissRequest = viewModel::dismissError,
                title = { Text(text = it) },
                confirmButton = {
                    Button(onClick = viewModel::dismissError) {
                        Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                    }
                }
            )
        }
    } else {
        Box(Modifier.fillMaxSize()) {
            LoginInPrompt(Modifier.align(Alignment.Center), navToSignIn, navToSignUp)
        }
    }
}

@Composable
fun PostsByTopic(
    topic: String,
    loading: Boolean,
    stories: List<SimpleStory>,
    viewMorePost: () -> Unit,
    navToPostDetail: (String) -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = topic,
                style = Typography.subtitle1,
                modifier = Modifier
                    .weight(1f, false)
                    .placeholder(
                        visible = loading,
                        highlight = PlaceholderHighlight.fade()
                    )
            )

            if (!loading)
                Text(
                    text = stringResource(resource = SharedRes.strings.stories_show_more),
                    modifier = Modifier
                        .clickable { viewMorePost() }
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                    fontSize = 10.sp,
                )
        }
        Spacer(modifier = Modifier.height(7.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stories) {
                StoryCard(
                    it.title,
                    it.content,
                    loading,
                    Modifier
                        .fillParentMaxWidth()
                        .placeholder(
                            visible = loading,
                            highlight = PlaceholderHighlight.fade()
                        )
                ) { navToPostDetail(it.id) }
            }
        }
    }
}


@Composable
fun LoginInPrompt(modifier: Modifier, navToSignIn: () -> Unit, navToSignUp: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(resource = SharedRes.strings.stories_auth_required_title))
        Button(onClick = navToSignUp, modifier = Modifier.padding(top = 26.dp)) {
            Text(text = stringResource(resource = SharedRes.strings.common__btn_sign_up))
        }
        OutlinedButton(onClick = navToSignIn, modifier = Modifier) {
            Text(text = stringResource(resource = SharedRes.strings.sign_in__btn_sign_in_instead))
        }
    }
}
