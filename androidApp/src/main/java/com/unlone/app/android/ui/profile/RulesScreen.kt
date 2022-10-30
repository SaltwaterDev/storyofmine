package com.unlone.app.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unlone.app.android.ui.comonComponent.StandardTopBar
import com.unlone.app.android.viewmodel.RulesViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    back: () -> Unit
) {

    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            StandardTopBar(title = stringResource(SharedRes.strings.rules_title)) {
                back()
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        Box(Modifier.fillMaxSize()) {
            if (state.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp, 30.dp)
                ) {
                    state.rules.forEachIndexed { index, s ->
                        Text(text = "${index + 1}. $s", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

        }
    }
}