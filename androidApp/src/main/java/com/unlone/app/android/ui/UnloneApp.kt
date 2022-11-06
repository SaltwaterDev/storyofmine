package com.unlone.app.android.ui

import android.util.Log
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
import kotlinx.coroutines.InternalCoroutinesApi


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
            Log.d("TAG", "UnloneApp: ${appState.navBackStackEntry.value?.destination}")
            Log.d("TAG", "UnloneApp: ${appState.shouldShowBottomBar}")
            AnimatedVisibility(visible = appState.shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = { UnloneBottomBar(appState) })
        }) { contentPadding ->
            MainNavHost(navController, Modifier.padding(contentPadding), appState::upPress)
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
                .height(48.dp)
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
                                painterResource(id = screen.icon), contentDescription = null
                            )
                        },
//                        label = { screen.label?.let { Text(it) } },
                        selected = currentDestination?.hierarchy?.any { (it.route) == screen.route } == true,
                        onClick = { appState.navigateToBottomBarRoute(screen.route) },
                    )
                }
            }
        )
    }
}
