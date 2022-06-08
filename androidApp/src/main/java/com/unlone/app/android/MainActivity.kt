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
import androidx.navigation.compose.rememberNavController
import com.unlone.app.android.ui.navigation.MainNavHost
import com.unlone.app.android.ui.navigation.UnloneBottomNav
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
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        // to decide when to hide/ show the bottom nav bar
        // val currentScreen = UnloneBottomNav.fromRoute(backstackEntry.value?.destination?.route)

        Scaffold(
            scaffoldState = rememberScaffoldState(),
            bottomBar = {
                BottomNavigation() {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.name) },
                            selected = currentDestination?.hierarchy?.any { (it.route) == screen.name } == true,
                            onClick = {
                                navController.navigate(screen.name) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
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

