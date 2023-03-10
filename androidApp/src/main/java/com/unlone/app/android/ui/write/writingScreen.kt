package com.unlone.app.android.ui.write

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.snap
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.comonComponent.PreviewBottomSheet
import com.unlone.app.android.ui.comonComponent.WriteScreenTopBar
import com.unlone.app.android.ui.connectivityState
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import com.unlone.app.android.viewmodel.WritingViewModel
import com.unlone.app.data.story.PublishStoryException
import com.unlone.app.domain.entities.NetworkState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.example.library.SharedRes


@Stable
class WritingScreenState(
    private val bodyText: String,
    private val setBodyText: (String) -> Unit,
) {
    val setBodyTextField: (TextFieldValue) -> Unit = {
        bodyTextField = it
        setBodyText(it.text)
    }

    // Other UI-scoped types
    var bodyTextField: TextFieldValue by mutableStateOf(
        TextFieldValue(
            bodyText,
            TextRange(bodyText.length)
        )
    )

    fun addImageMD(uri: Uri?) {
        uri?.let {
            val imageMD = "![image]($it)"
            setBodyText(bodyText + imageMD)
        }
    }
}

@Composable
fun rememberWritingScreenState(
    bodyText: String,
    setBodyText: (String) -> Unit,
): WritingScreenState =
    remember(bodyText, setBodyText) {
        WritingScreenState(bodyText, setBodyText)
    }


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
    val screenState = rememberWritingScreenState(
        bodyText = uiState.body,
        setBodyText = viewModel.setBody
    )

    val networkState by connectivityState()
    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current
    var showPostingDialog by remember { mutableStateOf(false) }
    var showNetworkUnavailableAlert by remember { mutableStateOf(false) }
    var requireSignInDialog by remember { mutableStateOf(false) }
    var toolbarHeight by remember { mutableStateOf(0.dp) }
    // launch for open gallery
    val loadGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            screenState.addImageMD(it)
        }

    LaunchedEffect(Unit){
        viewModel.resetShouldCreateNewVersionDraft()
    }

    LaunchedEffect(networkState) {
        viewModel.checkAuthentication()
        viewModel.refreshData(
            networkState is NetworkState.Available,
            draftIdArg,
            versionArg,
        )
    }

    // close preview when keyboard is shown
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            scaffoldState.bottomSheetState.collapse()
        }
    }

    LaunchedEffect(uiState.postSuccess) {
        if (uiState.postSuccess) {
            onPostSucceed()
        }
    }

    DisposableEffect(Unit){
        onDispose { viewModel.cleanUpState() }
    }


    BottomSheetScaffold(
        modifier = Modifier
            .displayCutoutPadding()
            .statusBarsPadding(),
        scaffoldState = scaffoldState,
        topBar = {
            WriteScreenTopBar(Modifier.statusBarsPadding(),
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
                    when {
                        networkState !is NetworkState.Available ->
                            showNetworkUnavailableAlert = true
                        !uiState.isUserSignedIn -> requireSignInDialog = true
                        else -> showPostingDialog = true
                    }
                })
        },
        sheetContent = {
            PreviewBottomSheet(title = uiState.title,
                content = screenState.bodyTextField.text,
                onClose = { scope.launch { scaffoldState.bottomSheetState.collapse() } })
        },
        sheetPeekHeight = 0.dp,
        drawerContent = {
            OptionsDrawer(
                uiState.draftList,
                clearAll = {
                    viewModel.clearDraft()
                    scope.launch { scaffoldState.drawerState.close() }
                }, newDraft = {
                    viewModel.createNewDraft()
                    scope.launch { scaffoldState.drawerState.close() }
                }, editHistory = {
                    uiState.currentDraftId?.let { navToEditHistory(it) }
                    scope.launch { scaffoldState.drawerState.close() }
                }, editHistoryEnabled = uiState.currentDraftId != null,
                switchDraft = {
                    scope.launch {
                        launch { it?.let { it1 -> viewModel.switchDraft(it1) } }
                        launch { scaffoldState.drawerState.close() }
                    }
                },
                deleteDraft = {
                    if (it == uiState.currentDraftId) {
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                    scope.launch { viewModel.deleteDraft(it) }
                },
                isCurrentDraft = { uiState.currentDraftId == it }
            )
        },
    ) { innerPadding ->

        Box(Modifier.fillMaxHeight()) {
            Column(
                Modifier.padding(innerPadding)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = uiState.loading, highlight = PlaceholderHighlight.fade()
                        ),
                    value = uiState.title,
                    onValueChange = viewModel::setTitle,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    placeholder = {
                        Text(text = stringResource(resource = SharedRes.strings.writing__placeholder))
                    },
                    textStyle = Typography.titleLarge,
                    readOnly = uiState.loading,
                )

                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (isKeyboardVisible) toolbarHeight else 0.dp)
                        .placeholder(
                            visible = uiState.loading, highlight = PlaceholderHighlight.fade()
                        ),
                    value = screenState.bodyTextField,
                    onValueChange = screenState.setBodyTextField,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    textStyle = Typography.body1,
                    readOnly = uiState.loading,
                )
            }

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
                        { scope.launch { viewModel.getDisplayingQuestion() } })
                }
            }


            if (showPostingDialog) PostingDialog(
                uiState.topicList,
                uiState.selectedTopic,
                viewModel.setTopic,
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


            if (uiState.storyPosting) Card(
                Modifier.align(Alignment.Center),
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(Modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(resource = SharedRes.strings.writing__posting),
                        modifier = Modifier.align(CenterVertically)
                    )
                    CircularProgressIndicator(Modifier.padding(start = 4.dp))
                }
            }

            uiState.error?.let {
                AlertDialog(
                    onDismissRequest = viewModel::dismiss,
                    title = { Text(text = stringResource(resource = SharedRes.strings.common__attention)) },
                    text = { Text(text = it) },
                    confirmButton = {
                        Button(
                            onClick = viewModel::dismiss
                        ) {
                            Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                        }
                    })
            }

            uiState.postStoryError?.let {
                AlertDialog(
                    onDismissRequest = viewModel::dismiss,
                    title = { Text(text = stringResource(resource = SharedRes.strings.common__attention)) },
                    text = { Text(text = stringResource(resource = getPostStoryErrorMessage(it))) },
                    confirmButton = {
                        Button(
                            onClick = viewModel::dismiss
                        ) {
                            Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                        }
                    })
            }

            if (requireSignInDialog) {
                RequireSignInDialog({ requireSignInDialog = false }, {
                    requireSignInDialog = false
                    navToSignIn()
                })
            }


            if (showNetworkUnavailableAlert) {
                AlertDialog(
                    onDismissRequest = { showNetworkUnavailableAlert = false },
                    title = { Text(text = stringResource(resource = SharedRes.strings.common__attention)) },
                    text = { Text(text = stringResource(resource = SharedRes.strings.common__network_unavailable_warning)) },
                    confirmButton = {
                        Button(
                            onClick = { showNetworkUnavailableAlert = false }
                        ) {
                            Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                        }
                    }
                )
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

