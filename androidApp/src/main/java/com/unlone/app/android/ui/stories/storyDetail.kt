package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.ui.comonComponent.CommentInput
import com.unlone.app.android.ui.comonComponent.CommentItem
import com.unlone.app.android.ui.comonComponent.StoryDetailTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import com.unlone.app.android.viewmodel.StoryDetailViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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


    val state by viewModel.state.collectAsState()
    var commentInputHeight by remember { mutableStateOf(0) }

    Box {
        Scaffold(
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
            },
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
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

                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(with(LocalDensity.current) { commentInputHeight.toDp() })
                )
            }
        }

        CommentInput(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .align(Alignment.BottomStart)
                .onGloballyPositioned {
                    commentInputHeight = it.size.height
                },
            comment = state.commentText,
            setComment = viewModel::setCommentText,
            onCommentSent = { storyId?.let { viewModel.postComment(storyId) } }
        )
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
