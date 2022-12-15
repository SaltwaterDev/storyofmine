package com.unlone.app.android.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.unlone.app.android.ui.navigation.UnloneBottomDestinations
import com.unlone.app.domain.entities.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
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
) =
    remember(scaffoldState, navController) {
        UnloneAppState(scaffoldState, navController)
    }

/**
 * Responsible for holding state related to [UnloneApp] and containing UI-related logic.
 */
@ExperimentalLayoutApi
@Stable
class UnloneAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
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
        Timber.d("navigateToBottomBarRoute: $currentRoute, $route")
        navController.navigate(route) {
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

    // todo
    fun navigateToStoriesDetail(pid: Long, from: NavBackStackEntry) {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val shouldShowNoNetworkSnackBar: Boolean
        @Composable get() {
            return connectivityState().value != NetworkState.Available
        }

}


@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<NetworkState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collect { value = it }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED


/**
 * Network utility to get current state of internet connection
 */
val Context.currentConnectivityState: NetworkState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(
    connectivityManager: ConnectivityManager
): NetworkState {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) NetworkState.Available else NetworkState.Unavailable
}

/**
 * Network Utility to observe availability or unavailability of Internet connection
 */
@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = NetworkCallback { connectionState -> trySend(connectionState) }

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, callback)

    // Set current state
    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    // Remove callback when not used
    awaitClose {
        // Remove listeners
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

fun NetworkCallback(callback: (NetworkState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(NetworkState.Available)
        }

        override fun onLost(network: Network) {
            callback(NetworkState.Unavailable)
        }
    }
}