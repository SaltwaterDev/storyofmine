package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.unlone.app.android.ui.findStartDestination
import com.unlone.app.android.ui.stories.ReportScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.stories.StoryDetail
import com.unlone.app.android.ui.stories.TopicDetail
import com.unlone.app.android.viewmodel.ReportViewModel
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.android.viewmodel.StoryDetailViewModel
import com.unlone.app.android.viewmodel.TopicDetailViewModel
import org.koin.androidx.compose.koinViewModel


@ExperimentalAnimationApi
fun NavGraphBuilder.storiesGraph(
    navController: NavHostController,
    navigateUp: () -> Unit,
) {

    navigation(
        startDestination = UnloneBottomDestinations.Stories.route,
        route = "story",
    ) {

        composable(
            UnloneBottomDestinations.Stories.route + "?requestedStoryId={requestedStoryId}",
            arguments = listOf(navArgument("requestedStoryId") {
                type = NavType.StringType
                nullable = true
            }),
        ) {
            val requestedStoryId: String? = it.arguments?.getString("requestedStoryId")

            val viewModel = koinViewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
                requestedStoryId = requestedStoryId,
                navToPostDetail = { navigateToStoryDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController, it) },
                navToSignIn = { navigateToSignInEmail(navController) },
                navToSignUp = { navigateToSignUp(navController) }
            )
        }

        composable(
            StoryDetail.routeWithArgs,
            arguments = StoryDetail.arguments
        ) {
            val pid: String? = it.arguments?.getString(StoryDetail.storyArg)
            val viewModel = koinViewModel<StoryDetailViewModel>()
            StoryDetail(
                pid,
                navigateUp,
                { topicId -> navigateToTopicDetail(navController, topicId) },
                {
                    if (pid != null) {
                        navToReport(navController, ReportType.story.name, pid)
                    }
                },
                viewModel
            )
        }
        composable(
            TopicDetail.routeWithArgs,
            arguments = TopicDetail.arguments
        ) {
            val topic = it.arguments?.getString(TopicDetail.topicArg)
            val viewModel = koinViewModel<TopicDetailViewModel>()
            TopicDetail(
                topic,
                navController::navigateUp,
                navToStoryDetail = { pid -> navigateToStoryDetail(navController, pid) },
                viewModel
            )
        }

        composable(
            Report.routeWithArgs,
            arguments = Report.arguments
        ) {
            val viewModel = koinViewModel<ReportViewModel>()
            val type = it.arguments?.getString(Report.reportTypeArg)
            val reported = it.arguments?.getString(Report.reportIdArg)

            ReportScreen(
                viewModel = viewModel,
                type = type,
                reported = reported,
                back = { navController.popBackStack() })
        }
    }
}


fun navigateToStoriesScreen(navController: NavHostController, sid: String) {
    navController.navigate(UnloneBottomDestinations.Stories.route + "?requestedStoryId=$sid}") {
        // Pop up backstack to the first destination and save state. This makes going back
        // to the start destination when pressing back in any other bottom tab.
        popUpTo(findStartDestination(navController.graph).id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun navigateToStoryDetail(navController: NavHostController, sid: String) {
    navController.navigate("${StoryDetail.route}/$sid")
}

fun navigateToTopicDetail(navController: NavHostController, topicId: String) {
    navController.navigate("${TopicDetail.route}/$topicId")
}

fun navToReport(navController: NavHostController, type: String, reported: String) {
    navController.navigate("${Report.route}/${type}/${reported}")
}
