package com.unlone.app.android.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unlone.app.android.ui.UnloneBottomNav
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.ui.lounge.PostDetail
import com.unlone.app.ui.lounge.TopicDetail
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.android.ui.write.WritingScreen
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.viewmodel.PostDetailViewModel
import com.unlone.app.android.viewmodel.WritingViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel


@OptIn(
    InternalCoroutinesApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {


    NavHost(
        navController = navController,
        startDestination = UnloneBottomNav.Write.name,
        modifier = modifier
    ) {

        composable(UnloneBottomNav.Write.name) {
            val viewModel by viewModel<WritingViewModel>()
            WritingScreen(viewModel)
        }

        composable(UnloneBottomNav.Stories.name) {
            val viewModel by viewModel<StoriesViewModel>()
            StoriesScreen(
                viewModel = viewModel,
                navToPostDetail = { navigateToPostDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController) },
                navToAuthGraph = { navigateToAuth(navController) }
            )
        }
        composable(UnloneBottomNav.Profile.name) {
            val viewModel by viewModel<ProfileViewModel>()
            ProfileScreen(viewModel)
        }
        composable(
            "post/{pid}",
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
                navController.navigate(UnloneBottomNav.Stories.name) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
            },
        )

        // todo: Add on-boarding Screens
    }
}


fun navigateToPostDetail(navController: NavHostController, pid: String) {
    navController.navigate("post/$pid")
}

fun navigateToTopicDetail(navController: NavHostController) {
    navController.navigate("topic")
}
