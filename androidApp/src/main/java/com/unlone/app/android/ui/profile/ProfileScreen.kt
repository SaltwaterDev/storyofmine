package com.unlone.app.android.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.ProfileViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    goToDraft: () -> Unit,
    goToMyStories: () -> Unit,
    goToSavedStories: () -> Unit,
    goToSetting: () -> Unit,
    goToHelp: () -> Unit,
    goToRules: () -> Unit,
) {

    val state = viewModel.state.collectAsState().value
    var showSignOutAlert by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        if (state.isUserLoggedIn) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = state.username,
                fontSize = 31.sp,
                modifier = Modifier
                    .padding(16.dp, 40.dp)
                    .placeholder(state.loading, highlight = PlaceholderHighlight.fade())
            )
        }

        if (!state.isUserLoggedIn)
            Spacer(modifier = Modifier.height(100.dp))

        if (state.isUserLoggedIn) {
            ProfileScreenDivider()
            ListItem(Modifier.clickable { goToMyStories() }) {
                Text(
                    text = stringResource(resource = SharedRes.strings.profile__my_stories),
                    style = Typography.subtitle1
                )
            }
            ProfileScreenDivider()
            ListItem(Modifier.clickable { goToSavedStories() }) {
                Text(
                    text = stringResource(
                        resource = SharedRes.strings.profile__saved
                    ), style = Typography.subtitle1
                )
            }
        }
        ProfileScreenDivider()
//        ListItem(Modifier.clickable { goToSetting() }) { Text(text = stringResource(resource = SharedRes.strings.profile__settings), style = Typography.subtitle1) }
//        ProfileScreenDivider()
//        ListItem(Modifier.clickable { goToHelp() }) {
//            Text(
//                text = stringResource(resource = SharedRes.strings.profile__help),
//                style = Typography.subtitle1
//            )
//        }
//        ProfileScreenDivider()
        ListItem(Modifier.clickable { goToRules() }) {
            Text(
                text = stringResource(resource = SharedRes.strings.profile__rules),
                style = Typography.subtitle1
            )
        }
        ProfileScreenDivider()
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = viewModel::switchLocaleZh) {
                Text(text = "change to zh")
            }
            Button(onClick = viewModel::switchLocaleEn) {
                Text(text = "change to en")
            }
        }
        ProfileScreenDivider()
        if (state.isUserLoggedIn) {
            ListItem(Modifier.clickable { showSignOutAlert = true }) {
                Text(
                    text = stringResource(
                        resource = SharedRes.strings.profile__sign_out
                    ), style = Typography.subtitle1
                )
            }
            ProfileScreenDivider()
        }
    }

    if (showSignOutAlert) {
        AlertDialog(
            onDismissRequest = { showSignOutAlert = false },
            title = { Text(text = stringResource(SharedRes.strings.profile__sign_out_alert_title)) },
            dismissButton = {
                TextButton(onClick = {
                    showSignOutAlert = false
                    viewModel.signOut()
                }) {
                    Text(text = stringResource(SharedRes.strings.profile__sign_out))
                }
            },
            confirmButton = {
                TextButton(onClick = { showSignOutAlert = false }) {
                    Text(text = stringResource(SharedRes.strings.common__btn_cancel))
                }
            },
        )
    }
}

@Composable
fun ProfileScreenDivider() {
    Divider(Modifier.fillMaxWidth(), color = MaterialTheme.colors.onSurface)
}