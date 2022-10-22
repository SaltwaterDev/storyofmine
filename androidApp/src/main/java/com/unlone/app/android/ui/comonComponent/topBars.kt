package com.unlone.app.android.ui.comonComponent

import android.text.style.LineBackgroundSpan.Standard
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.R
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.example.library.SharedRes
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
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = openOptions) {
                Icon(
                    Icons.Rounded.Menu,
                    contentDescription = "options"
                )
            }
            TextButton(onClick = openPreview) {
                Text(text = stringResource(resource = SharedRes.strings.writing__preview))
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
    save: (() -> Unit)? = null,
    saveEnabled: Boolean,
    traceHistory: () -> Unit,
    edit: () -> Unit,
    topic: String,
    isSelfWritten: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }


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
//                todo
//                save?.let {
//                    IconButton(onClick = it, enabled = saveEnabled) {
//                        Icon(
//                            Icons.Rounded.Bookmark,
//                            contentDescription = "save"
//                        )
//                    }
//                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Rounded.MoreVert,
                            contentDescription = "more"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = report) {
                            Text("Report")
                        }
                        /*DropdownMenuItem(onClick = { *//* Handle settings! *//* }) {
                            Text("Settings")
                        }
                        Divider()
                        DropdownMenuItem(onClick = { *//* Handle send feedback! *//* }) {
                            Text("Send Feedback")
                        }*/
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
        saveEnabled = true,
        traceHistory = { /*TODO*/ },
        edit = { /*TODO*/ },
        topic = "Topic",
        isSelfWritten = false,
    )
}

@Composable
fun TopicDetailTopBar(
    back: () -> Unit,
    topicTitle: String,
    isFollowing: Boolean,
    toggleFollowing: () -> Unit
) {
    TopAppBar() {

        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
            Text(
                text = topicTitle,
                color = contentColorFor(backgroundColor = MaterialTheme.colors.primarySurface)
            )
        }
        // todo: follow button
//        if (isFollowing)
//            TextButton(onClick = { toggleFollowing() }) {
//                Text(text = stringResource(resource = SharedRes.strings.common__btn_following))
//            }
//        else
//            Button(onClick = { toggleFollowing() }) {
//                Text(text = stringResource(resource = SharedRes.strings.common__btn_follow))
//            }
    }
}

@Preview
@Composable
fun TopicDetailTopBarPreview() {
    TopicDetailTopBar(
        {},
        "Apple",
        true,
        {}
    )
}


@Composable
fun StandardTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPressed: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            onBackPressed?.let { onClicked ->
                IconButton(onClick = onClicked) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                }
            }
        }
    )
}
