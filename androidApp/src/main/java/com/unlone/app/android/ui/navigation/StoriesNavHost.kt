package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.unlone.app.android.ui.stories.*
import com.unlone.app.android.viewmodel.*
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
            UnloneBottomDestinations.Stories.routeWithArgs,
            arguments = UnloneBottomDestinations.Stories.arguments,
        ) {
            val requestedStoryId: String? = it.arguments?.getString("requestedStoryId")

            val viewModel = koinViewModel<StoriesViewModel>()
            StoriesScreen(viewModel = viewModel,
                requestedStoryId = requestedStoryId,
                navToStoryDetail = { navigateToStoryDetail(navController, it) },
                navToTopicPosts = { navToTopicDetail(navController, it) },
                navToSignIn = { navigateToSignInEmail(navController) },
                navToSignUp = { navigateToSignUp(navController) },
                navToFullTopic = { navToAllTopic(navController) })
        }

        composable(
            StoryDetail.routeWithArgs, arguments = StoryDetail.arguments
        ) {
            val pid: String? = it.arguments?.getString(StoryDetail.storyArg)
            val viewModel = koinViewModel<StoryDetailViewModel>()
            StoryDetail(pid, navigateUp, { topicId -> navToTopicDetail(navController, topicId) }, {
                if (pid != null) {
                    navToReport(navController, ReportType.story.name, pid)
                }
            }, viewModel
            )
        }
        composable(
            TopicDetail.routeWithArgs, arguments = TopicDetail.arguments
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
            Report.routeWithArgs, arguments = Report.arguments
        ) {
            val viewModel = koinViewModel<ReportViewModel>()
            val type = it.arguments?.getString(Report.reportTypeArg)
            val reported = it.arguments?.getString(Report.reportIdArg)

            ReportScreen(viewModel = viewModel,
                type = type,
                reported = reported,
                back = { navController.popBackStack() })
        }

        composable(
            FullTopic.route
        ) {
            val viewModel = koinViewModel<FullTopicViewModel>()
            LaunchedEffect(Unit) { viewModel.getAllTopic() }

            val uiState = viewModel.uiState.collectAsState().value
            FullTopicScreen(uiState, back = navigateUp, navToTopicDetail = {
                navToTopicDetail(navController, it)
            })
        }
    }
}

fun navigateToStoryDetail(navController: NavHostController, sid: String) {
    navController.navigate("${StoryDetail.route}/$sid")
}

fun navToTopicDetail(navController: NavHostController, topicName: String) {
    navController.navigate("${TopicDetail.route}/$topicName")
}

fun navToReport(navController: NavHostController, type: String, reported: String) {
    navController.navigate("${Report.route}/${type}/${reported}")
}

fun navToAllTopic(navController: NavHostController) {
    navController.navigate(FullTopic.route)
}
