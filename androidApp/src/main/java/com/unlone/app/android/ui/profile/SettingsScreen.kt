package com.unlone.app.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import com.unlone.app.android.viewmodel.SettingsViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    back: () -> Unit
) {

    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            StandardTopBar(title = stringResource(SharedRes.strings.rules_title)) {
                back()
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = viewModel::switchLocaleZh) {
                    Text(text = "change to zh")
                }
                Button(onClick = viewModel::switchLocaleEn) {
                    Text(text = "change to en")
                }
            }
        }
    }
}