package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unlone.app.android.viewmodel.StoryDetailViewModel

@Composable
fun StoryDetail(
    postId: String?,
    back: () -> Unit,
    navToTopicDetail: (String) -> Unit,
    viewModel: StoryDetailViewModel
) {

    LaunchedEffect(Unit) {
        if (postId != null) {
            viewModel.getStoryDetail(postId)
        }
    }
    val state = viewModel.state.collectAsState().value


    Scaffold(
        modifier = Modifier.displayCutoutPadding(),
        topBar = {
            TopAppBar(
                Modifier.statusBarsPadding()
            ) {
                Box(
                    Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = back) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }

                    TextButton(
                        onClick = { navToTopicDetail("") },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = state.topic)
                    }

                    Row(Modifier.align(Alignment.CenterEnd)) {
                        if (state.isSelfWritten) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "report"
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Face, contentDescription = "save")
                            }
                        } else {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "History"
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text(text = state.title)
            Text(text = state.timestamp.toString())
            Text(text = state.content)

            Spacer(modifier = Modifier.height(50.dp))

            state.comments.forEach { _ ->
                CommentBlock()
            }
        }
    }

    state.errorMsg?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(text = it) },
            confirmButton = {
                Button(onClick = {
                    viewModel.dismissError()
                    back()
                }) {
                    Text(text = "Confirm")
                }
            }
        )
    }
}

@Composable
fun CommentBlock() {
    /*TODO("Not yet implemented")*/
}
