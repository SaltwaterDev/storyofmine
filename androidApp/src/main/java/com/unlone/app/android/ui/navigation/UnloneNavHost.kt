package com.unlone.app.android.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.unlone.app.android.ui.UnloneBottomDestinations
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.android.ui.write.WritingScreen
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.android.viewmodel.WritingViewModel
import com.unlone.app.ui.lounge.PostDetail
import com.unlone.app.ui.lounge.TopicDetail
import com.unlone.app.viewmodel.PostDetailViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel


@ExperimentalMaterialApi
@ExperimentalLayoutApi
@OptIn(
    InternalCoroutinesApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class, ExperimentalAnimatedInsets::class
)
@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = UnloneBottomDestinations.Write.route,
        modifier = modifier
    ) {

        composable(UnloneBottomDestinations.Write.route) {
            val viewModel by viewModel<WritingViewModel>()
            WritingScreen(
                viewModel,
                navToEditHistory = {
                    navToEditHistory()
                },
                navToSignIn = {
                    navToStories()
                }
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
            "${UnloneBottomDestinations.Profile.route}/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) {
            val viewModel by viewModel<PostDetailViewModel>()
            PostDetail(
                { navController.popBackStack() }, {}, viewModel
            )
        }
        composable("topic") {
            TopicDetail()
        }

        authGraph(
            navController,
            onSigninOrSignupFinished = {
                navController.navigate(UnloneBottomDestinations.Stories.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
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
    navController.navigate("post/$pid")
}

fun navigateToTopicDetail(navController: NavHostController) {
    navController.navigate("topic")
}
