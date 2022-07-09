package com.unlone.app.android.ui

import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.ui.navigation.MainNavHost
import com.unlone.app.ui.theme.UnloneTheme
import kotlinx.coroutines.InternalCoroutinesApi


@ExperimentalMaterialApi
@OptIn(ExperimentalLayoutApi::class)
@InternalCoroutinesApi
@Composable
fun UnloneApp() {
    val systemUiController: SystemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false
    UnloneTheme {
        val appState = rememberUnloneAppState()
        val navController = appState.navController
        Scaffold(
            scaffoldState = appState.scaffoldState,
            modifier = Modifier.navigationBarsPadding(),
            bottomBar = {
                Log.d("TAG", "UnloneApp: ${appState.shouldShowBottomBar}")
                if (appState.shouldShowBottomBar)
                    UnloneBottomBar(appState)
            }
        ) { contentPadding ->
            MainNavHost(navController, Modifier.padding(contentPadding))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnloneBottomBar(
    appState: UnloneAppState
) {
    val navController = appState.navController
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        appState.bottomBarTabs.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.name) },
                selected = currentDestination?.hierarchy?.any { (it.route) == screen.name } == true,
                onClick = { appState.navigateToBottomBarRoute(screen.route) }
            )
        }
    }
}