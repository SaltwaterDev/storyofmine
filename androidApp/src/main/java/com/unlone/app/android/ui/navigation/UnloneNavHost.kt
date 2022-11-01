package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.insets.ExperimentalAnimatedInsets
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
    NavHost(
        navController = navController,
        startDestination = UnloneBottomDestinations.Write.route,
        modifier = modifier,
        route = "main",
    ) {

        authGraph(
            navController,
            onSigninOrSignupFinished = { lastRoute ->
                navController.popBackStack(
                    route = lastRoute,
                    inclusive = true,
                    saveState = false
                )
            },
        )

        writeGraph(navController)
        storiesGraph(navController, navigateUp)
        profileGraph(navController, navigateUp)


        // todo: Add on-boarding Screens
    }
}

