package com.unlone.app.android.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.viewmodel.ProfileItemList
import com.unlone.app.android.viewmodel.ProfileViewModel


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {

    val state = viewModel.state.collectAsState().value

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
            is ProfileItemList.Logout -> logout()
        }
    }

    if (!state.loading)
        Column {
            Text(text = "_username", fontSize = 31.sp, modifier = Modifier.padding(28.dp, 30.dp))

            Spacer(modifier = Modifier.height(30.dp))

            state.profileItemList.forEach { item ->
                Divider(Modifier.fillMaxWidth())
                if (!item.requireLoggedIn || state.isUserLoggedIn) {
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.takeAction() }
                            .padding(15.dp))

                    Divider(Modifier.fillMaxWidth())
                }
            }
        }
}