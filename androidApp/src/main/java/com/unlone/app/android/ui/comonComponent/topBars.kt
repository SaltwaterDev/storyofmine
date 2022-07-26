package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
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

    val squareSize = 60.dp
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states
    val scope = rememberCoroutineScope()

    val systemUiController: SystemUiController = rememberSystemUiController()
    val topBarModifier = if (swipeableState.currentValue == 1)
        modifier
    else
        modifier.displayCutoutPadding()


    if (swipeableState.currentValue == 0 || swipeableState.currentValue == 1)
        LaunchedEffect(swipeableState.currentValue) {
            if (swipeableState.currentValue == 1) {
                systemUiController.isSystemBarsVisible = false
            }
            if (swipeableState.currentValue == 0)
                systemUiController.isSystemBarsVisible = true
        }

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.isSystemBarsVisible = true
            scope.launch { swipeableState.animateTo(0) }
        }
    }


    TopAppBar(
        modifier = topBarModifier
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .offset { IntOffset(0, -swipeableState.offset.value.roundToInt()) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = openOptions) {
                Icon(painterResource(id = R.drawable.ic_menu), contentDescription = "options")
            }
            OutlinedButton(onClick = openPreview) {
                Text(text = "Preview")
            }
            IconButton(onClick = post) {
                Icon(painterResource(id = R.drawable.ic_send), contentDescription = "post")
            }
        }
    }
}

@Preview
@Composable
fun WriteScreenTopBarPreview() {
    WriteScreenTopBar(Modifier, {}, {}, {})
}

@Composable
fun StoryDetailTopBar(
    back: () -> Unit,
    navToTopicDetail: () -> Unit,
    report: () -> Unit,
    save: () -> Unit,
    traceHistory: () -> Unit,
    edit: () -> Unit,
    topic: String,
    isSelfWritten: Boolean,
) {
    TopAppBar(
        Modifier.statusBarsPadding()
    ) {
        Box(
            Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = back) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            OutlinedButton(
                onClick = navToTopicDetail,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = topic)
            }

            Row(Modifier.align(Alignment.CenterEnd)) {
                if (!isSelfWritten) {
                    IconButton(onClick = report) {
                        Icon(
                            painterResource(id = R.drawable.ic_round_outlined_flag_24),
                            contentDescription = "report"
                        )
                    }
                    IconButton(onClick = save) {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_bookmark_border_24),
                            contentDescription = "save"
                        )
                    }
                } else {
                    IconButton(onClick = traceHistory) {
                        Icon(
                            painterResource(id = R.drawable.ic_history),
                            contentDescription = "History"
                        )
                    }
                    IconButton(onClick = edit) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StoryDetailTopBarPreview() {
    StoryDetailTopBar(
        back = { /*TODO*/ },
        navToTopicDetail = { /*TODO*/ },
        report = { /*TODO*/ },
        save = { /*TODO*/ },
        traceHistory = { /*TODO*/ },
        edit = { /*TODO*/ },
        topic = "Topic",
        isSelfWritten = false,
    )
}

