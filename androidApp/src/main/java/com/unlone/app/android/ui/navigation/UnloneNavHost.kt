package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.unlone.app.android.ui.findStartDestination
import com.unlone.app.android.ui.profile.MyStoriesScreen
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.profile.RulesScreen
import com.unlone.app.android.ui.stories.ReportScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.stories.StoryDetail
import com.unlone.app.android.ui.stories.TopicDetail
import com.unlone.app.android.viewmodel.*
import org.koin.androidx.compose.koinViewModel


@ExperimentalAnimationApi
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
    AnimatedNavHost(
        navController = navController,
        startDestination = UnloneBottomDestinations.Write.route,
        modifier = modifier,
        route = "main",
    ) {

        writeGraph(navController)

        composable(
            UnloneBottomDestinations.Stories.route,
        ) {
            val viewModel = koinViewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
                navToPostDetail = { navigateToStoryDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController, it) },
                navToAuthGraph = {
                    navigateToAuth(
                        navController,
                        UnloneBottomDestinations.Stories.route,
                    )
                }
            )
        }
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
            RulesScreen() {
                navigateUp()
            }
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

        authGraph(
            navController,
            onSigninOrSignupFinished = { lastRoute ->
                navController.popBackStack(
                    route = lastRoute!!,
                    inclusive = true,
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


fun navToStories() {
    /*TODO("Not yet implemented")*/
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

fun navToRules(navController: NavHostController) {
    navController.navigate(Rules.route)
}

fun navToMyStories(navController: NavHostController) {
    navController.navigate(MyStories.route)
}

