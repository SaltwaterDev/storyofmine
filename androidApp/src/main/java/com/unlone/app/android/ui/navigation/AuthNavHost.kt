package com.unlone.app.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.unlone.app.android.ui.auth.LoginScreen
import com.unlone.app.android.viewmodel.LoginViewModel
import kotlinx.coroutines.InternalCoroutinesApi


enum class AuthNav {
    Login,
    Registration
}


@OptIn(InternalCoroutinesApi::class)
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onLogin: () -> Unit,
    onReg: () -> Unit,
) {
    navigation(AuthNav.Login.name, route = "auth"){

        composable(AuthNav.Login.name) {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                onLoginSuccess = onLogin,
                navToReg = onReg,
                viewModel = viewModel
            )
        }
        composable(AuthNav.Registration.name) {
            /*ProfileScreen()*/
        }

        // todo: Add on-boarding Screens
    }
}

fun navigateToAuth(navController: NavHostController) {
    navController.navigate("auth")
}
