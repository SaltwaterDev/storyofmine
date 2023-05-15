package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.unlone.app.android.ui.profile.*
import com.unlone.app.android.viewmodel.*
import org.koin.androidx.compose.koinViewModel


@ExperimentalAnimationApi
fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
    navigateUp: () -> Unit,
) {

    navigation(
        startDestination = UnloneBottomDestinations.Profile.route,
        route = "profiles",
    ) {

        composable(
            UnloneBottomDestinations.Profile.route,
        ) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(
                viewModel,
                {},
                { navToMyStories(navController) },
                { navToSavedStories(navController) },
                { navToSetting(navController)} ,
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
            SavedStories.route,
        ) {
            val viewModel = koinViewModel<SavedStoriesViewModel>()
            SavedStoriesScreen(viewModel, { navigateToStoryDetail(navController, it) }, navigateUp)
        }

        composable(
            Rules.route,
        ) {
            val viewModel = koinViewModel<RulesViewModel>()
            RulesScreen(viewModel) {
                navigateUp()
            }
        }

        composable(
            Settings.route,
        ) {
            val viewModel = koinViewModel<SettingsViewModel>()
            SettingsScreen(viewModel) {
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

fun navToSetting(navController: NavHostController) {
    navController.navigate(Settings.route)
}


fun navToMyStories(navController: NavHostController) {
    navController.navigate(MyStories.route)
}

fun navToSavedStories(navController: NavHostController) {
    navController.navigate(SavedStories.route)
}

