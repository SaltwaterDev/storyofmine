package com.unlone.app.android.ui.stories

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unlone.app.android.ui.comonComponent.StandardTopBar
import com.unlone.app.android.viewmodel.FullTopicUiState
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullTopicScreen(
    uiState: FullTopicUiState,
    navToTopicDetail: (String) -> Unit,
    back: () -> Unit
) {

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            StandardTopBar(
                title = stringResource(resource = SharedRes.strings.common__topic),
                onBackPressed = back
            )
        }) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            uiState.topics.forEach {
                ListItem(Modifier.clickable {
                    navToTopicDetail(it.name)
                }) { Text(text = it.name) }
            }
        }
    }
}