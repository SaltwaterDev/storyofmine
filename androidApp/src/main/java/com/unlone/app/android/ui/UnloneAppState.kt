package com.unlone.app.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.unlone.app.android.ui.navigation.UnloneBottomDestinations
import timber.log.Timber


/**
 * Remembers and creates an instance of [UnloneAppState]
 */
@ExperimentalAnimationApi
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun rememberUnloneAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberAnimatedNavController(),
    storiesScreenListState: LazyListState = rememberLazyListState(),
) =
    remember(scaffoldState, navController, storiesScreenListState) {
        UnloneAppState(scaffoldState, navController, storiesScreenListState)
    }

/**
 * Responsible for holding state related to [UnloneApp] and containing UI-related logic.
 */
@ExperimentalLayoutApi
@Stable
class UnloneAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    val storiesScreenListState: LazyListState,
) {

    // ----------------------------------------------------------
    // BottomBar state source of truth
    // ----------------------------------------------------------

    val bottomBarTabs = UnloneBottomDestinations.values()

    // Reading this attribute will cause recompositions when the bottom bar needs shown, or not.
    // Not all routes need to show the bottom bar.
    val shouldShowBottomBar: Boolean
        @Composable get() = !WindowInsets.isImeVisible &&
                bottomBarTabs.any { batTab ->
                    currentRoute?.startsWith(batTab.route) ?: false
                }

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry: State<NavBackStackEntry?>
        @Composable get() = navController.currentBackStackEntryAsState()

    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        navController.navigate(route) {
            Timber.d("navigateToBottomBarRoute: $currentRoute, $route")
            navController.navigate(route) {
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // todo
    fun navigateToStoriesDetail(pid: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate("${UnloneBottomDestinations.Stories}/$pid")
        }
    }

}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
