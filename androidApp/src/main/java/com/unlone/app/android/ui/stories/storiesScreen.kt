package com.unlone.app.android.ui.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.data.story.SimpleStory
import com.unlone.app.android.ui.comonComponent.HorizontalScrollPosts

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
            Scaffold() { innerPadding ->
                LazyColumn(
                    Modifier.padding(innerPadding)
                ) {
                    item {
                        Text(
                            text = "Hello",
                            modifier = Modifier.padding(15.dp, 40.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    state.postsByTopics?.let { posts ->
                        items(posts) {
                            PostsByTopic(it.topic, it.stories, navToTopicPosts) { pid ->
                                navToPostDetail(pid)
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostsByTopic(
    title: String,
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
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Show more",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .clickable { viewMorePost() },
                    color = Color.Black.copy(0.6f)
                )
            }

            HorizontalScrollPosts(
                modifier = Modifier.width(this@BoxWithConstraints.maxWidth.times(0.95f)),
                stories
            ) { navToPostDetail(it) }
        }
    }
}


@Composable
fun LoginInPrompt(modifier: Modifier, navToAuth: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Sign up to see other stories")
        Button(onClick = navToAuth) {
            Text(text = "SignUp")
        }
    }
}
