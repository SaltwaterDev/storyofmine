package com.unlone.app.android.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.unlone.app.android.ui.auth.LoginScreen
import com.unlone.app.android.ui.auth.SignUpScreen
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.android.viewmodel.SignUpViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel


enum class AuthNav {
    Login,
    SignUp
}


@OptIn(InternalCoroutinesApi::class)
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onLogin: () -> Unit,
    onReg: () -> Unit,
) {
    navigation(AuthNav.Login.name, route = "auth") {

        composable(AuthNav.Login.name) {
            val viewModel by viewModel<SignInViewModel>()
            LoginScreen(
                onLoginSuccess = onLogin,
                navToSignUp = { navigateToSignUp(navController) },
                viewModel = viewModel
            )
        }
        composable(AuthNav.SignUp.name) {
            val viewModel by viewModel<SignUpViewModel>()
            SignUpScreen(
                viewModel = viewModel,
                onRegSuccess = onReg,
            )
        }
    }
}

fun navigateToAuth(navController: NavHostController) {
    navController.navigate("auth")
}

fun navigateToSignUp(navController: NavHostController) {
    navController.navigate("signup")
}
