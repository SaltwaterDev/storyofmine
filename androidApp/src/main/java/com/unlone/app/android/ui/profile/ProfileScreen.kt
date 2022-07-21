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
import com.unlone.app.android.viewmodel.ProfileItemList
import com.unlone.app.android.viewmodel.ProfileViewModel


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
    fun logout() = viewModel.signOut()

    fun ProfileItemList.takeAction() {
        when (this) {
            is ProfileItemList.MyStories -> goToMyStories()
            is ProfileItemList.Saved -> goToSavedStories()
            is ProfileItemList.Setting -> goToSetting()
            is ProfileItemList.Help -> goToHelp()
            is ProfileItemList.Logout -> {
                showSignOutAlert = true
            }
        }
    }


    Column {
        Text(
            text = state.username,
            fontSize = 31.sp,
            modifier = Modifier
                .padding(20.dp, 30.dp)
                .placeholder(state.loading, highlight = PlaceholderHighlight.fade())
        )

        Spacer(modifier = Modifier.height(30.dp))

        state.profileItemList.forEach { item ->
            Divider(Modifier.fillMaxWidth())
            if (!item.requireLoggedIn || state.isUserLoggedIn) {
                Text(
                    text = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            item.takeAction()
                        }
                        .padding(15.dp)
                        .placeholder(state.loading, highlight = PlaceholderHighlight.fade())
                )

                Divider(Modifier.fillMaxWidth())
            }
        }
    }

    if (showSignOutAlert)
        AlertDialog(
            onDismissRequest = { showSignOutAlert = false },
            text = { Text(text = "Are you sure to sign out?") },
            dismissButton = {
                TextButton(onClick = { viewModel.signOut() }) {
                    Text(text = "Sign out")
                }
            },
            confirmButton = {
                TextButton(onClick = { showSignOutAlert = false }) {
                    Text(text = "Cancel")
                }
            },
        )
}