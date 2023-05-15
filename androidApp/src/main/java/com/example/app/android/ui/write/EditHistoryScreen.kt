package com.example.app.android.ui.write

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.android.ui.comonComponent.StoryCard
import com.example.app.android.ui.comonComponent.TransparentTopBar
import com.example.app.android.ui.theme.Typography
import com.example.app.android.viewmodel.EditHistoryViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.example.library.SharedRes

@Composable
fun EditHistoryScreen(
    id: String?,
    viewModel: EditHistoryViewModel,
    openDraft: (String) -> Unit,
    back: () -> Unit,
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) {
        id?.let { viewModel.loadDraftVersions(id) }
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
    ) {

        TransparentTopBar(
            stringResource(resource = SharedRes.strings.edit_history_title),
        ) { back() }

        Spacer(modifier = Modifier.height(30.dp))

        state.drafts.forEach {
            val datetime = Instant.fromEpochSeconds(it.timeStamp).toLocalDateTime(
                currentSystemDefault()
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "${datetime.dayOfMonth}/${datetime.monthNumber}/${datetime.year}",
                    style = Typography.subtitle1
                )
                Spacer(modifier = Modifier.height(4.dp))
                StoryCard(
                    title = it.title,
                    content = it.content,
                    loading = state.loading,
                    onClick = { openDraft(it.version) })
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}