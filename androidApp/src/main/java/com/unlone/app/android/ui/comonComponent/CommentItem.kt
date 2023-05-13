package com.unlone.app.android.ui.comonComponent

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.domain.entities.Comment
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@ExperimentalMaterialApi
@Composable
fun CommentItem(comment: Comment) {
    var unread by remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) unread = !unread
        it != DismissValue.DismissedToEnd
    })

    val isDismissingComment = dismissState.dismissDirection != null
    Card(
        elevation = animateDpAsState(if (isDismissingComment) 4.dp else 0.dp).value,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        ListItem(
            text = {
                Text(
                    comment.username, fontWeight = if (unread) FontWeight.Bold else null
                )
            },
            secondaryText = { Text(comment.text, lineHeight = 16.sp, maxLines = 20) },
            singleLineSecondaryText = false
        )
    }
    /*SwipeToDismiss(
        state = dismissState,
        modifier = Modifier.padding(vertical = 4.dp),
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
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
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Done
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
                        Text(
                            comment.username,
                            fontWeight = if (unread) FontWeight.Bold else null
                        )
                    },
                    secondaryText = { Text(comment.text) },
                    singleLineSecondaryText = false
                )
            }
        }
    )*/
}


@ExperimentalComposeUiApi
@Composable
fun CommentInput(
    modifier: Modifier = Modifier,
    comment: String,
    sendEnabled: Boolean,
    setComment: (String) -> Unit,
    onCommentSent: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val enabled by remember { mutableStateOf(sendEnabled && comment.isNotBlank()) }

    Surface(modifier) {
        Row(
            Modifier.navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = comment,
                onValueChange = setComment,
                maxLines = 20,
                modifier = Modifier.weight(1f),
                enabled = sendEnabled,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Unspecified,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                ),
                trailingIcon = {
                    TextButton(modifier = Modifier.padding(4.dp), border = BorderStroke(
                        1.dp, MaterialTheme.colors.primary.copy(
                            alpha = if (enabled) 1f else 0.38f
                        )
                    ), enabled = sendEnabled && comment.isNotBlank(), onClick = {
                        onCommentSent()
                        keyboardController?.hide()
                    }) {
                        Text(text = stringResource(resource = SharedRes.strings.story_detail__send_reply))
                    }
                },
                textStyle = Typography.body1,
                placeholder = {
                    Text(
                        text = stringResource(resource = SharedRes.strings.story_detail__reply),
                        modifier = Modifier.align(CenterVertically),
                        style = Typography.body1,
                    )
                }
            )
        }
    }
}
