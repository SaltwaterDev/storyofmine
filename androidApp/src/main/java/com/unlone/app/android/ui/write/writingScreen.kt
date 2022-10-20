package com.unlone.app.android.ui.write

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.unlone.app.android.ui.comonComponent.PreviewBottomSheet
import com.unlone.app.android.ui.comonComponent.WriteScreenTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import com.unlone.app.android.viewmodel.WritingViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.example.library.SharedRes


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@ExperimentalAnimatedInsets
@ExperimentalLayoutApi
@ExperimentalMaterialApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    draftId: String?,
    draftVersionId: String?,
    navToEditHistory: (String) -> Unit,
    navToSignIn: () -> Unit,
) {
    val uiState = viewModel.state.collectAsState().value
    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current
    var showPostingDialog by remember { mutableStateOf(false) }
    var requireSignInDialog by remember { mutableStateOf(false) }
    var toolbarHeight by remember { mutableStateOf(0.dp) }
    // launch for open gallery
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.addImageMD(it)
    }

    LaunchedEffect(draftId) {
        viewModel.refreshData(draftId, draftVersionId)
        viewModel.getIsUserSignedIn()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.saveDraft()
            scope.launch { viewModel.refreshData() }
        }
    }

    // close preview when keyboard is shown
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            scaffoldState.bottomSheetState.collapse()
        }
    }


    BottomSheetScaffold(
        modifier = Modifier
            .displayCutoutPadding()
            .statusBarsPadding(),
        scaffoldState = scaffoldState,
        topBar = {
            WriteScreenTopBar(
                Modifier.statusBarsPadding(),
                { scope.launch { scaffoldState.drawerState.open() } },
                {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            scaffoldState.bottomSheetState.expand()
                            keyboardController?.hide()
                        } else scaffoldState.bottomSheetState.collapse()
                    }
                },
                {
                    if (uiState.isUserSignedIn)
                        showPostingDialog = true
                    else
                        requireSignInDialog = true

                }
            )
        },
        sheetContent = {
            PreviewBottomSheet(
                title = uiState.title,
                content = uiState.body.text,
                onClose = { scope.launch { scaffoldState.bottomSheetState.collapse() } }
            )
        },
        sheetPeekHeight = 0.dp,
        drawerContent = {
            OptionsDrawer(
                uiState.draftList,
                clearAll = {
                    viewModel.clearBody()
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
                },
                deleteDraft = viewModel::deleteDraft
            )
        },
    ) { innerPadding ->

        Box() {
            Column(
                Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
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
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    placeholder = { Text(text = stringResource(resource = SharedRes.strings.writing__placeholder)) },
                    textStyle = Typography.titleLarge
                )

                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (isKeyboardVisible) imeToolBarHeight.dp else 0.dp),
                    value = uiState.body,
                    onValueChange = { viewModel.setBody(it.text) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    textStyle = Typography.body1
                )
            }

            DisplayingQuestionBlock(
                uiState.displayingGuidingQuestion?.text,
                Modifier
                    .imePadding()
                    .align(Alignment.BottomStart)
                    .padding(
                        16.dp,
                        toolbarHeight + 8.dp
                    )
            )

            Crossfade(
                targetState = isKeyboardVisible,
                modifier = Modifier
                    .imePadding()
                    .align(Alignment.BottomStart)
                    .onGloballyPositioned {
                        val height = it.size.height
                        toolbarHeight = with(density) { height.toDp() }
                    },
            ) {
                if (isKeyboardVisible) {
                    WritingScreenToolBar(
                        { launcher.launch("image/*") },
                        { scope.launch { viewModel.getDisplayingQuestion() } }
                    )
                }
            }

            if (showPostingDialog)
                PostingDialog(
                    uiState.topicList,
                    uiState.selectedTopic,
                    viewModel::setTopic,
                    { showPostingDialog = false },
                    uiState.isPublished,
                    uiState.commentAllowed,
                    uiState.saveAllowed,
                    viewModel::setPublished,
                    viewModel::setCommentAllowed,
                    viewModel::setSaveAllowed,
                    {
                        scope.launch {
                            launch { scaffoldState.bottomSheetState.expand() }
                            launch { showPostingDialog = false }
                            launch { keyboardController?.hide() }
                        }
                    },
                    {
                        viewModel.postStory()
                        showPostingDialog = false
                    },
                )


            if (uiState.loading)
                Card(Modifier.align(Alignment.Center)) {
                    Row(Modifier.padding(8.dp)) {
                        Text(
                            text = stringResource(resource = SharedRes.strings.writing__posting),
                            modifier = Modifier.align(CenterVertically)
                        )
                        CircularProgressIndicator(Modifier.padding(start = 4.dp))
                    }
                }

            if (uiState.postSuccess)
                Dialog(
                    onDismissRequest = viewModel::dismiss,
                ) {
                    Card {
                        Text(
                            text = stringResource(resource = SharedRes.strings.writing__post_success),
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }

            uiState.error?.let {
                AlertDialog(
                    onDismissRequest = viewModel::dismiss,
                    title = { Text(text = stringResource(resource = SharedRes.strings.common__error)) },
                    text = { Text(text = it) },
                    confirmButton = {
                        Button(
                            onClick = viewModel::dismiss
                        ) {
                            Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                        }
                    }
                )
            }

            if (requireSignInDialog) {
                RequireSignInDialog(
                    { requireSignInDialog = false },
                    {
                        requireSignInDialog = false
                        navToSignIn()
                    }
                )
            }
        }
    }
}


const val imeToolBarHeight = 50