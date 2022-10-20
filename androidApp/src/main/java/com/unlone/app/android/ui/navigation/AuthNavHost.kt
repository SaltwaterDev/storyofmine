package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.unlone.app.android.ui.auth.signin.SignInEmailScreen
import com.unlone.app.android.ui.auth.signin.SignInPasswordScreen
import com.unlone.app.android.ui.auth.signup.EmailVerificationScreen
import com.unlone.app.android.ui.auth.signup.SetUsernameScreen
import com.unlone.app.android.ui.auth.signup.SignUpScreen
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.android.viewmodel.SignUpViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel


enum class AuthNav {
    SignIn,
    SignUp
}


@ExperimentalAnimationApi
@OptIn(InternalCoroutinesApi::class)
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onSigninOrSignupFinished: (String?) -> Unit,
) {


    navigation(
        AuthNav.SignUp.name,
        route = "auth/{lastRoute}",
        arguments = listOf(navArgument("lastRoute") { type = NavType.StringType })
    ) {

        val lastRoute = "auth/{lastRoute}"

        composable(AuthNav.SignUp.name) {
            val viewModelStoreOwner = remember { navController.getBackStackEntry("auth/{lastRoute}") }
            val viewModel = koinViewModel<SignUpViewModel>(owner = viewModelStoreOwner)

            SignUpScreen(
                viewModel = viewModel,
                navToSendEmailOtp = { navigateToEmailVerification(navController) },
                navToSignIn = { navigateToSignInEmail(navController) }
            )
        }
        composable(AuthNav.SignUp.name + "/setUsername") {
            val viewModelStoreOwner = remember { navController.getBackStackEntry("auth/{lastRoute}") }
            val viewModel = koinViewModel<SignUpViewModel>(owner = viewModelStoreOwner)

            SetUsernameScreen(
                viewModel = viewModel,
                onSignUpSuccess = { onSigninOrSignupFinished(lastRoute) },
            )
        }

        composable(AuthNav.SignUp.name + "/emailVerification") {
            val viewModelStoreOwner = remember { navController.getBackStackEntry("auth/{lastRoute}") }
            val viewModel = koinViewModel<SignUpViewModel>(owner = viewModelStoreOwner)

            EmailVerificationScreen(
                state = viewModel.uiState,
                onCancelSignUp = {
                    navController.popBackStack()
                },

                setOtp = viewModel.setOtp,
                navToSetUsername = { navigateToSetUsername(navController) },
                onOtpVerified = { viewModel.verifyOtp() },
                onOtpGenerate = { viewModel.generateOtp() },
                dismissError = { viewModel.dismissErrorMsg() }
            )
        }

        composable(
            AuthNav.SignIn.name + "/email",
        ) {
            val viewModelStoreOwner = remember { navController.getBackStackEntry("auth/{lastRoute}") }
            val viewModel = koinViewModel<SignInViewModel>(owner = viewModelStoreOwner)
            SignInEmailScreen(
                navToSignInPw = { navigateToSignInPw(navController) },
                navToSignUp = { navigateToSignUp(navController) },
                viewModel = viewModel
            )
        }
        composable(
            AuthNav.SignIn.name + "/password",
        ) {
            val viewModelStoreOwner = remember { navController.getBackStackEntry("auth/{lastRoute}") }
            val viewModel = koinViewModel<SignInViewModel>(owner = viewModelStoreOwner)
            SignInPasswordScreen(
                onSignInSuccess = { onSigninOrSignupFinished(lastRoute) },
                back = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}

fun navigateToAuth(
    navController: NavHostController,
    lsatRoute: String
) {
    navController.navigate("auth/$lsatRoute")
}

fun navigateToSignUp(navController: NavHostController) {
    navController.navigate(AuthNav.SignUp.name)
}

fun navigateToSignInEmail(navController: NavHostController) {
    navController.navigate(AuthNav.SignIn.name + "/email")
}

fun navigateToSignInPw(navController: NavHostController) {
    navController.navigate(AuthNav.SignIn.name + "/password")
}

fun navigateToSetUsername(navController: NavHostController) {
    navController.navigate(AuthNav.SignUp.name + "/setUsername")
}

fun navigateToEmailVerification(navController: NavHostController) {
    navController.navigate(AuthNav.SignUp.name + "/emailVerification")
}
