package com.unlone.app.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unlone.app.android.ui.navigation.MainNavHost
import com.unlone.app.android.ui.theme.UnloneTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import org.example.library.SharedRes
import timber.log.Timber


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@OptIn(ExperimentalLayoutApi::class)
@InternalCoroutinesApi
@Composable
fun UnloneApp() {

    UnloneTheme {
        val appState = rememberUnloneAppState()
        val navController = appState.navController

        Scaffold(scaffoldState = appState.scaffoldState, bottomBar = {
            Timber.d("UnloneApp: " + appState.navBackStackEntry.value?.destination)
            AnimatedVisibility(visible = appState.shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = { UnloneBottomBar(appState) })
        }) { contentPadding ->
            MainNavHost(
                appState,
                navController,
                Modifier.padding(contentPadding),
                appState::upPress
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnloneBottomBar(
    appState: UnloneAppState,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = BottomNavigationDefaults.Elevation,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                // tune system bar color
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                appState.bottomBarTabs.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                painterResource(id = screen.icon),
                                contentDescription = null
                            )
                        },
                        label = { screen.label?.let { Text(getBottomBarItemLabel(it)) } },
                        selected = currentDestination?.hierarchy?.any { (it.route) == screen.routeWithArgs } == true,
                        onClick = { appState.navigateToBottomBarRoute(screen.routeWithArgs) },
                    )
                }
            }
        )
    }
}

@Composable
fun getBottomBarItemLabel(label: String): String {
    return when (label) {
        "write" -> stringResource(resource = SharedRes.strings.bottom_nav_bar_label__write)
        "stories" -> stringResource(resource = SharedRes.strings.bottom_nav_bar_label__stories)
        "profile" -> stringResource(resource = SharedRes.strings.bottom_nav_bar_label__profile)
        else -> stringResource(resource = SharedRes.strings.common__error)
    }
}
