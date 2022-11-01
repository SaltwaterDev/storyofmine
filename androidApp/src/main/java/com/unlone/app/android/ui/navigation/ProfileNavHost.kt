package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.unlone.app.android.ui.auth.signin.SignInEmailScreen
import com.unlone.app.android.ui.auth.signin.SignInPasswordScreen
import com.unlone.app.android.ui.auth.signup.EmailVerificationScreen
import com.unlone.app.android.ui.auth.signup.SetUsernameScreen
import com.unlone.app.android.ui.auth.signup.SignUpScreen
import com.unlone.app.android.ui.profile.MyStoriesScreen
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.profile.RulesScreen
import com.unlone.app.android.viewmodel.*
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel



@ExperimentalAnimationApi
fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
    navigateUp: () -> Unit,
) {


    navigation(
        startDestination = UnloneBottomDestinations.Profile.route,
        route = "profile",
    ) {

        composable(
            UnloneBottomDestinations.Profile.route,
        ) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(
                viewModel,
                {},
                { navToMyStories(navController) },
                {},
                {},
                {},
                { navToRules(navController) })
        }


        composable(
            MyStories.route,
        ) {
            val viewModel = koinViewModel<MyStoriesViewModel>()
            MyStoriesScreen(viewModel, { navigateToStoryDetail(navController, it) }, navigateUp)
        }

        composable(
            Rules.route,
        ) {
            val viewModel = koinViewModel<RulesViewModel>()
            RulesScreen(viewModel) {
                navigateUp()
            }
        }
    }
}

fun navigateToProfile(
    navController: NavHostController,
) {
    navController.navigate("profile")
}


fun navToRules(navController: NavHostController) {
    navController.navigate(Rules.route)
}


fun navToMyStories(navController: NavHostController) {
    navController.navigate(MyStories.route)
}

