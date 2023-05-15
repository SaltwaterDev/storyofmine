package com.unlone.app.android.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unlone.app.android.ui.comonComponent.UnloneBottomBar
import com.unlone.app.android.ui.navigation.MainNavHost
import com.unlone.app.android.ui.theme.UnloneTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import org.example.library.SharedRes
import timber.log.Timber


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnloneApp() {

    UnloneTheme {
        val appState = rememberUnloneAppState()

        Scaffold(
            scaffoldState = appState.scaffoldState,
        ) {
            val contentPadding by animateDpAsState(it.calculateBottomPadding())

            MainNavHost(
                appState,
                Modifier.padding(bottom = contentPadding),
                appState::upPress
            )

            Box(Modifier.fillMaxSize()) {
                if (false) {
//                FIXME: if (appState.shouldShowNoNetworkSnackBar) {
                    Snackbar(
                        modifier = Modifier
                            .padding(bottom = contentPadding)
                            .padding(8.dp)
                            .align(Alignment.BottomStart)
                    ) { Text(text = stringResource(resource = SharedRes.strings.common__network_unavailable_warning)) }
                }
            }
        }
    }
}
