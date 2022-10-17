package com.unlone.app.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import com.unlone.app.android.viewmodel.EditHistoryViewModel
import com.unlone.app.android.viewmodel.MyStoriesViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.example.library.SharedRes

@Composable
fun MyStoriesScreen(
    viewModel: MyStoriesViewModel,
    openStory: (String) -> Unit,
    back: () -> Unit,
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.loadMyStories()
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        item {
            Row {
                IconButton(onClick = back) {
                    Icon(Icons.Rounded.ArrowBack, "back")
                }

                Text(
                    text = stringResource(resource = SharedRes.strings.my_stories_title),
                    style = Typography.titleLarge,
                    modifier = Modifier.align(CenterVertically)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }


        items(state.stories, { it.id }) {
            val datetime = Instant.fromEpochMilliseconds(it.createdDate).toLocalDateTime(
                currentSystemDefault()
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "${datetime.dayOfMonth}/${datetime.monthNumber}/${datetime.year}",
                    style = Typography.subtitle1
                )
                Spacer(modifier = Modifier.height(4.dp))
                StoryCard(title = it.title,
                    content = it.content,
                    loading = state.loading,
                    onClick = { openStory(it.id) })
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