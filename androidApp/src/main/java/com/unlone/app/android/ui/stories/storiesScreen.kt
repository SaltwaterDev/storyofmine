package com.unlone.app.android.ui.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.unlone.app.android.ui.comonComponent.Post
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


    if (!state.isUserLoggedIn)
        Box(Modifier.fillMaxSize()) {
            LoginInPrompt(Modifier.align(Alignment.Center), navToAuthGraph)
        }
    else {
        viewModel.checkAuth()   // to ensure again user has authorized
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
                            .padding(16.dp, 40.dp)
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

@Composable
fun PostsByTopic(
    title: String,
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
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f, false)
                    .placeholder(
                        visible = loading,
                        highlight = PlaceholderHighlight.fade()
                    )
            )

            Text(
                text = "Show more",
                fontSize = 10.sp,
                modifier = Modifier
                    .clickable { viewMorePost() }
                    .placeholder(
                        visible = loading,
                        highlight = PlaceholderHighlight.fade()
                    ),
                color = Color.Black.copy(0.6f)
            )
        }
        Spacer(modifier = Modifier.height(7.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stories) {
                Post(
                    it.title,
                    it.content,
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
fun LoginInPrompt(modifier: Modifier, navToAuth: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Sign up to see other stories")
        Button(onClick = navToAuth, modifier = Modifier.padding(26.dp)) {
            Text(text = "SignUp")
        }
    }
}
