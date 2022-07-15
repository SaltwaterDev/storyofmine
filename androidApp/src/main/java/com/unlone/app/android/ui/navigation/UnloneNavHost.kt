package com.unlone.app.android.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.unlone.app.android.ui.UnloneBottomDestinations
import com.unlone.app.android.ui.findStartDestination
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.write.WritingScreen
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.android.viewmodel.WritingViewModel
import com.unlone.app.android.ui.stories.PostDetail
import com.unlone.app.ui.lounge.TopicDetail
import com.unlone.app.android.viewmodel.PostDetailViewModel
import org.koin.androidx.compose.viewModel


@ExperimentalMaterialApi
@ExperimentalLayoutApi
@OptIn(
    ExperimentalAnimatedInsets::class
)
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = UnloneBottomDestinations.Write.route,
        modifier = modifier,
        route = "main",
    ) {

        Log.d("wesley", "MainNavHost: ${navController.currentDestination}")

        composable(UnloneBottomDestinations.Write.route) {
            val viewModel by viewModel<WritingViewModel>()
            WritingScreen(
                viewModel,
                navToEditHistory = { navToEditHistory() },
                navToSignIn = { navigateToAuth(navController) },
            )
        }

        composable(UnloneBottomDestinations.Stories.route) {
            val viewModel by viewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
                navToPostDetail = { navigateToPostDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController) },
                navToAuthGraph = { navigateToAuth(navController) }
            )
        }
        composable(UnloneBottomDestinations.Profile.route) {
            val viewModel by viewModel<ProfileViewModel>()
            ProfileScreen(viewModel)
        }
        composable(
            "${UnloneBottomDestinations.Stories.route}/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) {
            val viewModel by viewModel<PostDetailViewModel>()
            PostDetail(
                navigateUp,
                { navigateToTopicDetail(navController) },
                viewModel
            )
        }
        composable("topic") {
            TopicDetail()
        }

        authGraph(
            navController,
            onSigninOrSignupFinished = {
                navController.popBackStack(
                    destinationId = findStartDestination(navController.graph).id,
                    inclusive = false,
                    saveState = false
                )
//                navController.navigate(UnloneBottomDestinations.Stories.route) {
//                    popUpTo(navController.graph.findStartDestination().id)
//                }
            },
        )

        // todo: Add on-boarding Screens
    }
}

fun navToEditHistory() {
    /*TODO("Not yet implemented")*/
}

fun navToStories() {
    /*TODO("Not yet implemented")*/
}


fun navigateToPostDetail(navController: NavHostController, pid: String) {
    navController.navigate("${UnloneBottomDestinations.Stories.route}/$pid")
}

fun navigateToTopicDetail(navController: NavHostController) {
    navController.navigate("topic")
}
