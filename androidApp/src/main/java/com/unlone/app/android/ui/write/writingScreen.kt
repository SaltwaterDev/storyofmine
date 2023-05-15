package com.unlone.app.android.ui.write

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.snap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.unlone.app.android.ui.comonComponent.PreviewBottomSheet
import com.unlone.app.android.ui.comonComponent.WriteScreenTopBar
import com.unlone.app.android.ui.connectivityState
import com.unlone.app.android.viewmodel.WritingViewModel
import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.domain.entities.NetworkState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.example.library.SharedRes


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalCoroutinesApi::class)
@ExperimentalComposeUiApi
@ExperimentalAnimatedInsets
@ExperimentalLayoutApi
@ExperimentalMaterialApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    draftIdArg: String? = null,
    versionArg: String? = null,
    navToEditHistory: (String) -> Unit,
    navToSignIn: () -> Unit,
    onPostSucceed: () -> Unit,
) {
    val uiState = viewModel.uiState
    val screenState =
        rememberWritingScreenState(bodyText = uiState.body, setBodyText = viewModel.setBody)

    val density = LocalDensity.current
    val networkState by connectivityState()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    var showPostingDialog by remember { mutableStateOf(false) }
    var showNetworkUnavailableAlert by remember { mutableStateOf(false) }
    var requireSignInDialog by remember { mutableStateOf(false) }
    var titleAndBodyIsEmptyDialog by remember { mutableStateOf(false) }
    var toolbarHeight by remember { mutableStateOf(0.dp) }
    // for opening gallery
    val loadGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            screenState.addImageMD(it)
        }

    val closeDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }
    val onPostBtnClick: () -> Unit = {
        when {
            networkState !is NetworkState.Available ->
                showNetworkUnavailableAlert = true
            !uiState.isUserSignedIn -> requireSignInDialog = true
            uiState.isTitleAndBodyEmpty -> titleAndBodyIsEmptyDialog = true
            else -> showPostingDialog = true
        }
    }

    LaunchedEffect(Unit) { viewModel.resetShouldCreateNewVersionDraft() }

    LaunchedEffect(networkState) {
        viewModel.refreshData(
            networkState is NetworkState.Available,
            draftIdArg,
            versionArg,
        )
    }

    if (isKeyboardVisible) {
        LaunchedEffect(isKeyboardVisible) {
            // close preview bottom sheet when keyboard is shown
            modalBottomSheetState.hide()
        }
    }

    if (uiState.postSuccess) {
        LaunchedEffect(uiState.postSuccess) {
            onPostSucceed()
        }
    }

    if (scaffoldState.drawerState.isOpen) {
        LaunchedEffect(scaffoldState.drawerState.isOpen) {
            keyboardController?.hide()
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.cleanUpState() }
    }

    ModalBottomSheetLayout(
        modifier = Modifier
            .background(Color.Red)
            .displayCutoutPadding()
            .statusBarsPadding(),
        sheetContent = {
            PreviewBottomSheet(
                title = uiState.title,
                content = screenState.bodyTextField.text,
                onClose = { scope.launch { modalBottomSheetState.hide() } })

        },
        sheetState = modalBottomSheetState
    ) {

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                WriteScreenTopBar(
                    modifier = Modifier.statusBarsPadding(),
                    openOptions = { scope.launch { scaffoldState.drawerState.open() } },
                    openPreview = {
                        scope.launch {
                            if (!modalBottomSheetState.isVisible) {
                                modalBottomSheetState.show()
                                keyboardController?.hide()
                            } else
                                modalBottomSheetState.hide()
                        }
                    },
                )
            },
            drawerContent = {
                OptionsDrawer(
                    uiState.draftList,
                    clearAll = {
                        viewModel.clearDraft()
                        closeDrawer()
                    }, newDraft = {
                        viewModel.createNewDraft()
                        closeDrawer()
                    }, editHistory = {
                        uiState.currentDraftId?.let { navToEditHistory(it) }
                        closeDrawer()
                    }, editHistoryEnabled = uiState.currentDraftId != null,
                    switchDraft = {
                        scope.launch {
                            launch { it?.let { it1 -> viewModel.switchDraft(it1) } }
                            launch { scaffoldState.drawerState.close() }
                        }
                    },
                    deleteDraft = {
                        if (it == uiState.currentDraftId) closeDrawer()
                        scope.launch { viewModel.deleteDraft(it) }
                    },
                    isCurrentDraft = { uiState.currentDraftId == it }
                )
            },
        ) { innerPadding ->

            Box(Modifier.fillMaxHeight()) {
                WrittenContentBlock(
                    innerPadding = innerPadding,
                    loading = uiState.loading,
                    title = uiState.title,
                    setTitle = viewModel::setTitle,
                    bodyTextField = screenState.bodyTextField,
                    setBodyTextField = screenState.setBodyTextField,
                    bottomPadding = if (isKeyboardVisible) toolbarHeight else 0.dp
                )

                DisplayingQuestionBlock(
                    uiState.displayingGuidingQuestion?.text,
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 16.dp, end = 16.dp, bottom = toolbarHeight + 8.dp
                        )
                )

                Crossfade(
                    targetState = isKeyboardVisible,
                    modifier = Modifier.align(Alignment.BottomStart),
                    animationSpec = snap()
                ) {
                    if (isKeyboardVisible) {
                        WritingScreenToolBar(
                            Modifier
                                .onGloballyPositioned {
                                    val height = it.size.height
                                    toolbarHeight = with(density) { height.toDp() }
                                }
                                .imePadding(),
                            { loadGalleryLauncher.launch("image/*") },
                            { })
                    }
                }


                val openPreviewFromPostingDialog: () -> Unit = {
                    scope.launch {
                        launch { modalBottomSheetState.show() }
                        launch { showPostingDialog = false }
                        launch { keyboardController?.hide() }
                    }
                }

                uiState.error?.let {
                    AlertDialog(
                        onDismissRequest = viewModel::dismiss,
                        title = { Text(text = stringResource(resource = SharedRes.strings.common__oops)) },
                        text = { Text(text = it) },
                        confirmButton = {
                            Button(onClick = viewModel::dismiss) {
                                Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                            }
                        })
                }

                uiState.postStoryError?.let {
                    PostStoryErrorAlert(it, viewModel::dismiss)
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

                if (titleAndBodyIsEmptyDialog)
                    TitleAndBodyIsEmptyAlert { titleAndBodyIsEmptyDialog = false }

                if (showNetworkUnavailableAlert)
                    NetworkUnavailableAlert { showNetworkUnavailableAlert = false }

            }
        }
    }
}


fun getPostStoryErrorMessage(publishStoryException: PublishStoryException): StringResource {
    return when (publishStoryException) {
        is PublishStoryException.EmptyTitleOrBodyException -> SharedRes.strings.error__publish_story_empty_title_or_body
        is PublishStoryException.EmptyTopicException -> SharedRes.strings.error__publish_story_empty_topic
    }
}

