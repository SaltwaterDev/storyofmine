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
import com.unlone.app.android.ui.stories.ReportScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.stories.StoryDetail
import com.unlone.app.android.ui.stories.TopicDetail
import com.unlone.app.android.viewmodel.*
import kotlinx.coroutines.InternalCoroutinesApi
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
            UnloneBottomDestinations.Stories.route,
        ) {
            val viewModel = koinViewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
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



fun navigateToStoryDetail(navController: NavHostController, pid: String) {
    navController.navigate("${StoryDetail.route}/$pid")
}

fun navigateToTopicDetail(navController: NavHostController, topicId: String) {
    navController.navigate("${TopicDetail.route}/$topicId")
}

fun navToReport(navController: NavHostController, type: String, reported: String) {
    navController.navigate("${Report.route}/${type}/${reported}")
}
