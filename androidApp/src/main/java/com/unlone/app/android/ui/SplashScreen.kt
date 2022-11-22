package com.unlone.app.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(
    onDelayEnded: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(duration = 1.seconds)
        onDelayEnded()
    }
    Column() {
        Text("This is Splash Screen. Will load 1 second")

    }

}