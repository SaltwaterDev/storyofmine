package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WriteScreenTopBar(
    modifier: Modifier = Modifier,
    openOptions: () -> Unit,
    openPreview: () -> Unit,
    post: () -> Unit,
) {

    val squareSize = 48.dp
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states
    val scope = rememberCoroutineScope()

    val systemUiController: SystemUiController = rememberSystemUiController()

    if (swipeableState.currentValue == 0 || swipeableState.currentValue == 1)
        LaunchedEffect(swipeableState.currentValue) {
            if (swipeableState.currentValue == 1)
                systemUiController.isSystemBarsVisible = false
            if (swipeableState.currentValue == 0)
                systemUiController.isSystemBarsVisible = true
        }

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.isSystemBarsVisible = true
            scope.launch { swipeableState.animateTo(0) }
        }
    }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .offset { IntOffset(0, -swipeableState.offset.value.roundToInt()) },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = openOptions) {
            Icon(painterResource(id = R.drawable.ic_menu), contentDescription = "options")
        }
        TextButton(onClick = openPreview) {
            Text(text = "Preview")
        }
        IconButton(onClick = post) {
            Icon(painterResource(id = R.drawable.ic_send), contentDescription = "post")
        }
    }
}

@Composable
fun WriteScreenTopBarPreview() {
    WriteScreenTopBar(Modifier, {}, {}, {})
}
