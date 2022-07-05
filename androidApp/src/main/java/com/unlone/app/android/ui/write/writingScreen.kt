package com.unlone.app.android.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.R
import com.unlone.app.android.viewmodel.WritingViewModel
import kotlinx.coroutines.launch


@ExperimentalAnimatedInsets
@ExperimentalLayoutApi
@ExperimentalMaterialApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    navToEditHistory: (String) -> Unit,
) {
    val uiState = viewModel.state.collectAsState().value
    val context = LocalContext.current

    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val systemUiController: SystemUiController = rememberSystemUiController()

    DisposableEffect(key1 = context) {
        systemUiController.isSystemBarsVisible = false
        onDispose {
            viewModel.saveDraft()
            systemUiController.isSystemBarsVisible = true
        }
    }


    Box {
        BottomSheetScaffold(
            modifier = Modifier.systemBarsPadding(),
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Text(text = "Options")
                        }
                        Button(onClick = { scope.launch { if (scaffoldState.bottomSheetState.isCollapsed) scaffoldState.bottomSheetState.expand() else scaffoldState.bottomSheetState.collapse() } }) {
                            Text(text = "Preview")
                        }

                        Button(onClick = {
                            viewModel.postStory()
                        }) {
                            Text(text = "Post")
                        }
                    }
                }
            },
            sheetContent = {
                PreviewBottomSheet(
                    title = uiState.title,
                    content = uiState.content,
                    onClose = { scope.launch { scaffoldState.bottomSheetState.collapse() } }
                )
            },
            sheetPeekHeight = 0.dp,
            drawerContent = {
                OptionsDrawer(
                    uiState.draftList,
                    clearAll = {
                        viewModel.clearTitleAndContent()
                        scope.launch { scaffoldState.drawerState.close() }
                    },
                    newDraft = {
                        viewModel.createNewDraft()
                        scope.launch { scaffoldState.drawerState.close() }
                    },
                    editHistory = {
                        uiState.currentDraftId?.let { navToEditHistory(it) }
                        scope.launch { scaffoldState.drawerState.close() }
                    },
                    switchDraft = {
                        viewModel.switchDraft(it)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .imeNestedScroll()
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    onValueChange = { viewModel.setTitle(it) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isKeyboardVisible) imeToolBarHeight.dp else 0.dp),
                    value = uiState.content,
                    onValueChange = { viewModel.setContent(it) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
            }
        }

        if (isKeyboardVisible)
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .imePadding()
                    .height(imeToolBarHeight.dp)
                    .fillMaxWidth()
                    .background(Color.Green)
            ) {

            }
    }

}

@Composable
fun PreviewBottomSheet(
    title: String,
    content: String,
    onClose: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
    ) {
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Icon(
                painter = painterResource(id = R.drawable.icon_close),
                contentDescription = "close",
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(text = title)
        Text(text = content)
    }
}


@Composable
fun OptionsDrawer(
    listOfDraft: Map<String, String>,
    clearAll: () -> Unit,
    editHistory: () -> Unit,
    newDraft: () -> Unit,
    switchDraft: (String) -> Unit,
) {
    Column {
        Text(text = "Options")
        Text(text = "Clear", modifier = Modifier
            .fillMaxWidth()
            .clickable { clearAll() }
            .padding(15.dp))
        Divider(Modifier.fillMaxWidth())
        Text(text = "Edit History", modifier = Modifier
            .fillMaxWidth()
            .clickable { editHistory() }
            .padding(15.dp))
        Divider(Modifier.fillMaxWidth())
        Text(text = "New Draft", modifier = Modifier
            .fillMaxWidth()
            .clickable { newDraft() }
            .padding(15.dp))
        Divider(Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(50.dp))


        listOfDraft.entries.forEach {
            Text(text = it.value, modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    switchDraft(it.key)
                }
                .padding(15.dp))
            Divider(Modifier.fillMaxWidth())
        }
    }
}


const val imeToolBarHeight = 50