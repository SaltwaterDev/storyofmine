package com.unlone.app.android.ui.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.comonComponent.HorizontalScrollPosts
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.data.story.SimpleStory

@Composable
fun StoriesScreen(
    viewModel: StoriesViewModel,
    navToPostDetail: (String) -> Unit = {},
    navToTopicPosts: () -> Unit = {},
    navToAuthGraph: () -> Unit = {},
) {

    val state by viewModel.state.collectAsState()

    if (!state.loading) {
        if (!state.isUserLoggedIn)
            Box(Modifier.fillMaxSize()) {
                LoginInPrompt(Modifier.align(Alignment.Center), navToAuthGraph)
            }
        else {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    item {
                        Text(
                            text = "Hello ${state.username ?: ""}",
                            modifier = Modifier
                                .padding(15.dp, 40.dp)
                                .placeholder(
                                    visible = state.loading,
                                    highlight = PlaceholderHighlight.fade()
                                ),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    state.postsByTopics?.let { posts ->
                        items(posts) {
                            PostsByTopic(
                                it.topic,
                                state.loading,
                                it.stories,
                                navToTopicPosts
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
                            Text(text = "Confirm")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PostsByTopic(
    title: String,
    loading: Boolean,
    stories: List<SimpleStory>,
    viewMorePost: () -> Unit,
    navToPostDetail: (String) -> Unit
) {
    BoxWithConstraints {
        Column {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(start = 15.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .placeholder(
                            visible = loading,
                            highlight = PlaceholderHighlight.fade()
                        )
                )

                Text(
                    text = "Show more",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .clickable { viewMorePost() }
                        .placeholder(
                            visible = loading,
                            highlight = PlaceholderHighlight.fade()
                        ),
                    color = Color.Black.copy(0.6f)
                )
            }

            HorizontalScrollPosts(
                modifier = Modifier.width(this@BoxWithConstraints.maxWidth.times(0.95f)),
                loading,
                stories,
            ) { navToPostDetail(it) }
        }
    }
}


@Composable
fun LoginInPrompt(modifier: Modifier, navToAuth: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Sign up to see other stories")
        Button(onClick = navToAuth, modifier = Modifier.padding(26.dp)) {
            Text(text = "SignUp")
        }
    }
}
