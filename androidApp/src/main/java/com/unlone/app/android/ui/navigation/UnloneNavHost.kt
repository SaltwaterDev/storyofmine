package com.unlone.app.android.ui.navigation

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
import com.unlone.app.android.viewmodel.ProfileViewModel
import com.unlone.app.android.ui.stories.StoriesScreen
import com.unlone.app.ui.lounge.PostDetail
import com.unlone.app.ui.lounge.TopicDetail
import com.unlone.app.android.ui.profile.ProfileScreen
import com.unlone.app.ui.write.WritingScreen
import com.unlone.app.android.viewmodel.StoriesViewModel
import com.unlone.app.viewmodel.PostDetailViewModel
import com.unlone.app.viewmodel.WritingViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel


enum class UnloneBottomNav(val icon: ImageVector) {

    Write(icon = Icons.Filled.Create),
    Stories(icon = Icons.Filled.Add),
    Profile(icon = Icons.Filled.Face);

    companion object {
        fun fromRoute(route: String?): UnloneBottomNav =
            when (route?.substringBefore("/")) {
                Write.name -> Write
                Stories.name -> Stories
                Profile.name -> Profile
                null -> Stories
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalComposeUiApi::class)
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
