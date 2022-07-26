package com.unlone.app.android.ui.stories

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.ui.comonComponent.StoryDetailTopBar
import com.unlone.app.android.ui.theme.Typography
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
            StoryDetailTopBar(
                back = back,
                navToTopicDetail = { navToTopicDetail(state.topic) },
                report = { /*TODO*/ },
                save = { /*TODO*/ },
                traceHistory = { /*TODO*/ },
                edit = { /*TODO*/ },
                topic = state.topic,
                isSelfWritten = state.isSelfWritten,
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(text = state.title, style = Typography.h1)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = state.createdDate, style = Typography.caption)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = state.content, style = Typography.body1, lineHeight = 32.sp, letterSpacing = 1.sp)
            }
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
