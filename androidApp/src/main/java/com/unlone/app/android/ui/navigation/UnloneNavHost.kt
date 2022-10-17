package com.unlone.app.android.ui.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.unlone.app.android.ui.UnloneBottomDestinations
import com.unlone.app.android.ui.findStartDestination
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.stories.StoryDetail
import com.unlone.app.android.ui.write.WritingScreen
import com.unlone.app.android.ui.stories.TopicDetail
import com.unlone.app.android.viewmodel.*
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.viewModel


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalLayoutApi
@OptIn(
    ExperimentalAnimatedInsets::class, ExperimentalComposeUiApi::class
)
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = UnloneBottomDestinations.Write.route,
        modifier = modifier,
        route = "main",
    ) {

        Log.d("wesley", "MainNavHost: ${navController.currentDestination}")

        composable(UnloneBottomDestinations.Write.route) {
            val viewModel = koinViewModel<WritingViewModel>()
            WritingScreen(
                viewModel,
                navToEditHistory = { navToEditHistory() },
                navToSignIn = { navigateToAuth(navController) },
            )
        }

        composable(
            UnloneBottomDestinations.Stories.route,
        ) {
            val viewModel = koinViewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
                navToPostDetail = { navigateToStoryDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController, it) },
                navToAuthGraph = { navigateToAuth(navController) }
            )
        }
        composable(
            UnloneBottomDestinations.Profile.route,
        ) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(viewModel)
        }
        composable(
            "${UnloneBottomDestinations.Stories.route}/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) {
            val pid: String? = it.arguments?.getString("pid")
            val viewModel = koinViewModel<StoryDetailViewModel>()
            StoryDetail(
                pid,
                navigateUp,
                { topicId -> navigateToTopicDetail(navController, topicId) },
                viewModel
            )
        }
        composable(
            "topic/{topic}",
            arguments = listOf(navArgument("topic") { type = NavType.StringType })
        ) {
            val topic = it.arguments?.getString("topic")
            val viewModel by viewModel<TopicDetailViewModel>()
            TopicDetail(
                topic,
                navController::navigateUp,
                navToStoryDetail = { pid -> navigateToStoryDetail(navController, pid) },
                viewModel
            )
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


fun navigateToStoryDetail(navController: NavHostController, pid: String) {
    navController.navigate("${UnloneBottomDestinations.Stories.route}/$pid")
}

fun navigateToTopicDetail(navController: NavHostController, topicId: String) {
    navController.navigate("topic/$topicId")
}
