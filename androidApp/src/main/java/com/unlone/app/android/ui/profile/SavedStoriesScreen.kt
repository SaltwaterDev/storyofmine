package com.unlone.app.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.comonComponent.TransparentTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.SavedStoriesViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.example.library.SharedRes

@Composable
fun SavedStoriesScreen(
    viewModel: SavedStoriesViewModel,
    openStory: (String) -> Unit,
    back: () -> Unit,
) {
    val state = viewModel.uiState

    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        item {
            TransparentTopBar(
                stringResource(resource = SharedRes.strings.saved_stories_title),
            ) { back() }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }


        items(state.stories, { it.id }) {
            val datetime = it.createdDate?.let { it1 ->
                Instant.fromEpochMilliseconds(it1).toLocalDateTime(
                    currentSystemDefault()
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                datetime?.let {
                    Text(
                        text = "${datetime.dayOfMonth}/${datetime.monthNumber}/${datetime.year}",
                        style = Typography.subtitle1
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                StoryCard(
                    title = it.title,
                    content = it.content,
                    loading = state.loading,
                    onClick = { openStory(it.id) },
                    modifier = Modifier.placeholder(
                        visible = state.loading,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    state.error?.let {
        AlertDialog(onDismissRequest = viewModel::dismissError,
            title = { Text(text = it) },
            confirmButton = {
                Button(
                    onClick = viewModel::dismissError
                ) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            }
        )
    }
}