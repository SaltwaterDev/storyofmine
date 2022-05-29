package com.unlone.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unlone.app.ui.lounge.LoungeScreen
import com.unlone.app.ui.lounge.PostDetail
import com.unlone.app.ui.lounge.TopicDetail
import com.unlone.app.ui.profile.ProfileScreen
import com.unlone.app.ui.write.WritingScreen
import com.unlone.app.viewmodel.LoungeViewModel
import com.unlone.app.viewmodel.PostDetailViewModel
import com.unlone.app.viewmodel.ProfileViewModel
import com.unlone.app.viewmodel.WritingViewModel
import kotlinx.coroutines.InternalCoroutinesApi


enum class UnloneBottomNav(val icon: ImageVector) {

    Write(
        icon = Icons.Filled.Create
    ),

    Lounge(
        icon = Icons.Filled.Add,
    ),
    Profile(
        icon = Icons.Filled.Face,
    );

    companion object {
        fun fromRoute(route: String?): UnloneBottomNav =
            when (route?.substringBefore("/")) {
                Write.name -> Write
                Lounge.name -> Lounge
                Profile.name -> Profile
                null -> Lounge
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

            val viewModel = hiltViewModel<WritingViewModel>()

            WritingScreen(viewModel)
        }

        composable(UnloneBottomNav.Lounge.name) {
            val viewModel = hiltViewModel<LoungeViewModel>()
            LoungeScreen(
                viewModel = viewModel,
                navToPostDetail = { navigateToPostDetail(navController, it) },
                navToTopicPosts = { navigateToTopicDetail(navController) },
                navToAuthGraph = { navigateToAuth(navController) }
            )
        }
        composable(UnloneBottomNav.Profile.name) {
            val viewModel = hiltViewModel<ProfileViewModel>()
            ProfileScreen(viewModel)
        }
        composable(
            "post/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) {
            val viewModel = hiltViewModel<PostDetailViewModel>()
            PostDetail(
                { navController.popBackStack() }, {}, viewModel
            )
        }
        composable("topic") {
            TopicDetail()
        }

        authGraph(navController,
            onLogin = { navController.popBackStack() },
            onReg = {}
        )
    }
}


fun navigateToPostDetail(navController: NavHostController, pid: String) {
    navController.navigate("post/$pid")
}

fun navigateToTopicDetail(navController: NavHostController) {
    navController.navigate("topic")
}
