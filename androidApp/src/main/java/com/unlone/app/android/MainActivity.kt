package com.unlone.app.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unlone.app.android.ui.UnloneBottomNav
import com.unlone.app.android.ui.navigation.MainNavHost
import com.unlone.app.android.ui.rememberUnloneAppState
import com.unlone.app.ui.theme.UnloneTheme
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnloneApp()
        }
    }
}


@InternalCoroutinesApi
@Composable
fun UnloneApp() {
    UnloneTheme {
        val items = UnloneBottomNav.values().toList()
        val appState = rememberUnloneAppState()
        val navController = appState.navController

        Scaffold(
            scaffoldState = appState.scaffoldState,
            bottomBar = {
                if (appState.shouldShowBottomBar)
                    BottomNavigation {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        items.forEach { screen ->
                            BottomNavigationItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.name) },
                                selected = currentDestination?.hierarchy?.any { (it.route) == screen.name } == true,
                                onClick = {
                                    navController.navigate(screen.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
            }
        ) { contentPadding ->
            MainNavHost(navController, Modifier.padding(contentPadding))
        }
    }
}


