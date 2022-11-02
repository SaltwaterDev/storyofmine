package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.unlone.app.android.ui.profile.MyStoriesScreen
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.profile.RulesScreen
import com.unlone.app.android.ui.profile.SavedStoriesScreen
import com.unlone.app.android.viewmodel.MyStoriesViewModel
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.viewmodel.RulesViewModel
import com.unlone.app.android.viewmodel.SavedStoriesViewModel
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
                { navToSavedStories(navController) },
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

fun navToSavedStories(navController: NavHostController) {
    navController.navigate(SavedStories.route)
}

