package com.unlone.app.android.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unlone.app.android.ui.comonComponent.StandardTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.RulesViewModel
import com.unlone.app.android.viewmodel.SettingsViewModel
import com.unlone.app.data.userPreference.UnloneLocale
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    back: () -> Unit
) {

    val state = viewModel.state.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            StandardTopBar(title = stringResource(SharedRes.strings.settings_title)) {
                back()
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(resource = SharedRes.strings.settings_language),
                    style = Typography.subtitle1,
                    modifier = Modifier.padding(16.dp)
                )
                UnloneLocale.values().forEach { locale ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = ((locale == (state.currentLocale ?: false))),
                                onClick = { viewModel.switchLocale(locale) }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        RadioButton(
                            selected = ((locale == (state.currentLocale ?: false))),
                            onClick = { viewModel.switchLocale(locale) }
                        )
                        Text(
                            text = getLocaleNameString(locale.localeName),
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getLocaleNameString(localeName: String): String {
    return when (localeName) {
        "zh" -> stringResource(resource = SharedRes.strings.settings_lang_zh)
        "en" -> stringResource(resource = SharedRes.strings.settings_lang_en)
        else -> stringResource(resource = SharedRes.strings.settings_lang_en)
    }
}

