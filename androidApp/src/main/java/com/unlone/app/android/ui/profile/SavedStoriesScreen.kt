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
import com.unlone.app.data.story.SimpleStory
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
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
                modifier = Modifier.padding(bottom = 30.dp)
            ) { back() }
        }

        items(state.stories, { it.id }) { simpleStory ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val formattedDateTime = getSimpleStoryDateTime(simpleStory)?.let {
                    "${it.dayOfMonth}/${it.monthNumber}/${it.year}"
                }

                Text(text = formattedDateTime ?: "", style = Typography.subtitle1)
                StoryCard(
                    title = simpleStory.title,
                    content = simpleStory.content,
                    loading = state.loading,
                    onClick = { openStory(simpleStory.id) },
                    modifier = Modifier
                        .placeholder(
                            visible = state.loading,
                            highlight = PlaceholderHighlight.fade()
                        )
                        .padding(top = 4.dp, bottom = 20.dp)
                )
            }
        }
    }

    state.error?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(text = it) },
            confirmButton = {
                Button(onClick = viewModel::dismissError) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            }
        )
    }
}

private fun getSimpleStoryDateTime(simpleStory: SimpleStory): LocalDateTime? {
    return simpleStory.createdDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(currentSystemDefault())
    }
}