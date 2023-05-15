package com.example.app.android.ui.write

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app.android.R
import com.example.app.android.ui.theme.Typography
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OptionsDrawer(
    listOfDraft: Map<String, String>,
    clearAll: () -> Unit,
    editHistory: () -> Unit,
    editHistoryEnabled: Boolean,
    newDraft: () -> Unit,
    switchDraft: (String?) -> Unit,
    deleteDraft: (String) -> Unit,
    isCurrentDraft: (String?) -> Boolean,
) {
    LazyColumn {
        item {
            Text(
                text = stringResource(resource = SharedRes.strings.writing__option_drawer_title),
                modifier = Modifier.padding(16.dp),
                style = Typography.h6
            )
        }

        item {
            BlockWithIcon(
                R.drawable.ic_clear,
                stringResource(resource = SharedRes.strings.writing__clear)
            ) { clearAll() }
            Divider(Modifier.fillMaxWidth())
        }

        item {
            BlockWithIcon(
                R.drawable.ic_history,
                stringResource(resource = SharedRes.strings.writing__edit_history),
                editHistoryEnabled,
            ) { editHistory() }
            Divider(Modifier.fillMaxWidth())
        }

        item {
            BlockWithIcon(
                R.drawable.ic_add,
                stringResource(resource = SharedRes.strings.writing__new_draft)
            ) { newDraft() }
            Divider(Modifier.fillMaxWidth())
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }

        if (listOfDraft.values.isNotEmpty())
            item {
                Text(
                    text = stringResource(resource = SharedRes.strings.writing__option_drawer_draft_title),
                    modifier = Modifier.padding(16.dp),
                    style = Typography.h6
                )
            }

        items(
            items = listOfDraft.toList(),
            key = { it.first },
        ) {
            DismissableBlockWithIcon(
                iconId = R.drawable.ic_write,
                title = it.second.ifEmpty { "(Untitled)" },
                modifier = Modifier.animateItemPlacement(),
                onClick = { switchDraft(it.first) },
                onDismiss = {
                    deleteDraft(it.first)
                },
                isCurrentDraft = isCurrentDraft(it.first),
            )
            Divider(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun BlockWithIcon(
    iconId: Int?,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconId?.let {
            Icon(
                painterResource(id = it),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .alpha(if (enabled) 1f else 0.38f)
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (enabled) 1f else 0.38f),
            style = Typography.subtitle1
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun DismissableBlockWithIcon(
    iconId: Int?,
    title: String,
    modifier: Modifier,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    isCurrentDraft: Boolean,
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) onDismiss()
            it != DismissValue.DismissedToEnd
        }
    )
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> Color.Red
                    DismissValue.DismissedToStart -> Color.Unspecified
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        }) {
        Surface(Modifier.offset(x = (10).dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                iconId?.let {
                    Icon(
                        painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Text(
                    text = title,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 26.dp),
                    style = Typography.subtitle1,
                    fontWeight = if (isCurrentDraft) FontWeight.SemiBold else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


