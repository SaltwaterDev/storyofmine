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
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.R
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

            }

        if (uiState.postSuccess)
            AlertDialog(
                onDismissRequest = viewModel::dismiss,
                title = {
                    Text(text = "Post Succeed")
                },
                buttons = {}
            )

        uiState.error?.let {
            AlertDialog(
                onDismissRequest = viewModel::dismiss,
                title = { Text(text = "Sign in required") },
                text = { Text(text = it) },
                confirmButton = {
                    Button(onClick = {
                        viewModel.dismiss()
                        navToSignIn()
                    }) {
                        Text(text = "Sign in to publish your story")
                    }
                }
            )
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
        Text(text = title, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(34.dp))
        Text(text = content, modifier = Modifier.padding(horizontal = 16.dp))
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
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(text = "Options", modifier = Modifier.padding(16.dp), style = Typography.h1)
            BlockWithIcon(R.drawable.ic_clear, "Clear") { clearAll() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_history, "Edit History") { editHistory() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_add, "New Draft") { newDraft() }
            Divider(Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(60.dp))
            listOfDraft.entries.forEach {
                BlockWithIcon(iconId = R.drawable.ic_write, title = it.value) {
                    switchDraft(it.key)
                }
                Divider(Modifier.fillMaxWidth())
            }
        }

    }
}

@Composable
private fun BlockWithIcon(iconId: Int?, title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        iconId?.let {
            Icon(
                painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = title, modifier = Modifier.padding(16.dp))
    }
}


const val imeToolBarHeight = 50