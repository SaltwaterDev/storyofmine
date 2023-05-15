package com.example.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.android.ui.MyStoriesAppState
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.nanoseconds


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalLayoutApi
@OptIn(
    ExperimentalAnimatedInsets::class
)
@Composable
fun MainNavHost(
    appState: MyStoriesAppState,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier,
        route = "main",
    ) {

        composable("splash") {
            LaunchedEffect(Unit) {
                delay(duration = 1.nanoseconds)
                navigateUp()
                navController.navigate(MyStoriesBottomDestinations.Write.routeWithArgs)
            }
        }

        // todo: Add on-boarding Screens

        writeGraph(
            navController,
            navToStories = {},
        )
    }
}

