package com.unlone.app.android.ui.stories

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.ui.comonComponent.StoryDetailTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.StoryDetailViewModel

@OptIn(ExperimentalMaterialApi::class)
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
        LazyColumn(
            Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(text = state.title, style = Typography.h1)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.createdDate, style = Typography.caption)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = state.content,
                        style = Typography.body1,
                        lineHeight = 32.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
            items(state.comments) {
                // This is an example of a list of dismissible items, similar to what you would see in an
                // email app. Swiping left reveals a 'delete' icon and swiping right reveals a 'done' icon.
                // The background will start as grey, but once the dismiss threshold is reached, the colour
                // will animate to red if you're swiping left or green if you're swiping right. When you let
                // go, the item will animate out of the way if you're swiping left (like deleting an email) or
                // back to its default position if you're swiping right (like marking an email as read/unread).
                CommentItem(it)
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

@ExperimentalMaterialApi
@Composable
fun CommentItem(comment: String) {
    var unread by remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) unread = !unread
            it != DismissValue.DismissedToEnd
        }
    )
    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier.padding(vertical = 4.dp),
        directions = setOf(StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> Color.Green
                    DismissValue.DismissedToStart -> Color.Red
                }
            )
            val alignment = when (direction) {
                StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                StartToEnd -> Icons.Default.Done
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        },
        dismissContent = {
            Card(
                elevation = animateDpAsState(
                    if (dismissState.dismissDirection != null) 4.dp else 0.dp
                ).value
            ) {
                ListItem(
                    text = {
                        Text(comment, fontWeight = if (unread) FontWeight.Bold else null)
                    },
                    secondaryText = { Text("Swipe me left or right!") }
                )
            }
        }
    )
}