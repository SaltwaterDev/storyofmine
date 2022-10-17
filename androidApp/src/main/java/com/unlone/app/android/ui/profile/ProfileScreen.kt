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
import com.unlone.app.android.viewmodel.ProfileViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {

    val state = viewModel.state.collectAsState().value
    var showSignOutAlert by remember { mutableStateOf(false) }


    //    fun goToDraft() {}
    fun goToMyStories() {}
    fun goToSavedStories() {}
    fun goToSetting() {}
    fun goToHelp() {}



    Column(Modifier.fillMaxSize()) {

        if (state.isUserLoggedIn) {
            Text(
                text = state.username,
                fontSize = 31.sp,
                modifier = Modifier
                    .padding(16.dp, 40.dp)
                    .placeholder(state.loading, highlight = PlaceholderHighlight.fade())
            )
        }

//        val text = stringResource(resource = SharedRes.strings.my_string)
//        Text(text = text)


        if (!state.isUserLoggedIn)
            Spacer(modifier = Modifier.height(100.dp))

        if (state.isUserLoggedIn) {
            ListItem(Modifier.clickable { goToMyStories() }) { Text(text = stringResource(resource = SharedRes.strings.profile__my_stories)) }
            Divider(Modifier.fillMaxWidth())
            ListItem(Modifier.clickable { goToSavedStories() }) { Text(text = stringResource(resource = SharedRes.strings.profile__saved)) }
            Divider(Modifier.fillMaxWidth())
        }
        ListItem(Modifier.clickable { goToSetting() }) { Text(text = stringResource(resource = SharedRes.strings.profile__settings)) }
        Divider(Modifier.fillMaxWidth())
        ListItem(Modifier.clickable { goToHelp() }) { Text(text = stringResource(resource = SharedRes.strings.profile__help)) }
        Divider(Modifier.fillMaxWidth())
        if (state.isUserLoggedIn) {
            ListItem(Modifier.clickable { showSignOutAlert = true }) { Text(text = stringResource(resource = SharedRes.strings.profile__sign_out)) }
            Divider(Modifier.fillMaxWidth())
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