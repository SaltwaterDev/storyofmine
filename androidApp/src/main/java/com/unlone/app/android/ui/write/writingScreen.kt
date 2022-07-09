package com.unlone.app.android.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.R
import com.unlone.app.android.ui.comonComponent.PreviewBottomSheet
import com.unlone.app.android.ui.comonComponent.WriteScreenTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.WritingViewModel
import kotlinx.coroutines.launch


@ExperimentalAnimatedInsets
@ExperimentalLayoutApi
@ExperimentalMaterialApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    navToEditHistory: (String) -> Unit,
    navToSignIn: () -> Unit,
) {
    val uiState = viewModel.state.collectAsState().value
    val context = LocalContext.current
    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    var showPostingDialog by remember { mutableStateOf(false) }


    DisposableEffect(key1 = context) {
        onDispose {
            viewModel.saveDraft()
        }
    }


    Box {
        BottomSheetScaffold(
            modifier = Modifier.systemBarsPadding(),
            scaffoldState = scaffoldState,
            topBar = {
                WriteScreenTopBar(
                    { scope.launch { scaffoldState.drawerState.open() } },
                    { scope.launch { if (scaffoldState.bottomSheetState.isCollapsed) scaffoldState.bottomSheetState.expand() else scaffoldState.bottomSheetState.collapse() } },
                    { showPostingDialog = true },
                )
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
            if (showPostingDialog)
                PostingDialog(
                    // todo: to be replaced
                    listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5"),
                    uiState.topic,
                    viewModel::setTopic,
                    { showPostingDialog = false },
                    uiState.isPublished,
                    uiState.commentAllowed,
                    uiState.saveAllowed,
                    viewModel::setPublished,
                    viewModel::setCommentAllowed,
                    viewModel::setSaveAllowed,
                    {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                        showPostingDialog = false
                    },
                    {
                        viewModel.postStory()
                        showPostingDialog = false
                    },
                )
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
                // todo
            }

        if (uiState.loading)
            Surface(Modifier.align(Alignment.Center)) {
                Row {
                    Text(text = "Posting")
                    CircularProgressIndicator()
                }
            }

        if (uiState.postSuccess)
            Dialog(
                onDismissRequest = viewModel::dismiss,
            ) {
                Surface {
                    Text(text = "Post Succeed")
                }
            }

        uiState.error?.let {
            AlertDialog(
                onDismissRequest = viewModel::dismiss,
                title = { Text(text = "Sign in required") },
                text = { Text(text = it) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.dismiss()
                            navToSignIn()
                        }
                    ) {
                        Text(text = "Sign in to publish your story")
                    }
                }
            )
        }
    }
}


const val imeToolBarHeight = 50