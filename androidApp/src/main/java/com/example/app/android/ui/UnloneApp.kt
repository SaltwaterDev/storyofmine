package com.example.app.android.ui

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.android.ui.navigation.MainNavHost
import com.example.app.android.ui.theme.UnloneTheme
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


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
