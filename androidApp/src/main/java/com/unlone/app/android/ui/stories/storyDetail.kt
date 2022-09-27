package com.unlone.app.android.ui.stories

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.ui.comonComponent.CommentItem
import com.unlone.app.android.ui.comonComponent.StoryDetailTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import com.unlone.app.android.viewmodel.StoryDetailViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetail(
    storyId: String?,
    back: () -> Unit,
    navToTopicDetail: (String) -> Unit,
    reportStory: () -> Unit,
    viewModel: StoryDetailViewModel
) {
    val systemUiController = rememberSystemUiController()
    val lightTheme = MaterialTheme.colors.isLight
    DisposableEffect(Unit) {
        if (lightTheme) {
            systemUiController.setStatusBarColor(
                color = Color.DarkGray,
                darkIcons = false
            )
        }
        onDispose {}
    }

    LaunchedEffect(Unit) {
        if (storyId != null) {
            viewModel.getStoryDetail(storyId)
        }
    }
    val state = viewModel.state.collectAsState().value

    Scaffold(
        modifier = Modifier.imePadding().displayCutoutPadding(),
        topBar = {
            StoryDetailTopBar(
                back = back,
                navToTopicDetail = { navToTopicDetail(state.topic) },
                report = reportStory,
                save = { /*TODO*/ },
                traceHistory = { /*TODO*/ },
                edit = { /*TODO*/ },
                topic = state.topic,
                isSelfWritten = state.isSelfWritten,
            )
        }
    ) { innerPadding ->

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = state.title,
                    style = Typography.titleLarge,
                    modifier = Modifier.placeholder(
                        visible = state.loading,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.createdDate, style = Typography.caption,
                    modifier = Modifier.placeholder(
                        visible = state.loading,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = state.content, style = Typography.body1,
                    modifier = Modifier.placeholder(
                        visible = state.loading,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.height(50.dp))
            }


            state.comments.forEach {
                CommentItem(it)
            }

            Row() {
                TextField(
                    value = state.commentText,
                    onValueChange = viewModel::setCommentText,
                    placeholder = { Text(text = "_comment here") },
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = {
                    if (storyId != null) {
                        viewModel.postComment(storyId)
                    }
                }) {
                    Text(text = "Send")
                }
            }
        }
    }

    state.errorMsg?.let {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissError()
                back()
            },
            title = { Text(text = it) },
            confirmButton = {
                Button(onClick = {
                    viewModel.dismissError()
                    back()
                }) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            }
        )
    }


}

