package com.unlone.app.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.nanoseconds

@Composable
fun SplashScreen(
    onDelayEnded: () -> Unit,
) {
    Box() {
        LaunchedEffect(Unit) {
            delay(duration = 1.nanoseconds)
            onDelayEnded()
        }
    }
}