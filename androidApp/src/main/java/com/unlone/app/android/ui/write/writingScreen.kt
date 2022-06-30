package com.unlone.app.android.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.viewmodel.WritingViewModel
import kotlinx.coroutines.launch


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
        onDispose {
            viewModel.saveDraft()
            systemUiController.isSystemBarsVisible = true
        }
    }

    LaunchedEffect(isKeyboardVisible) {
        systemUiController.isSystemBarsVisible = !isKeyboardVisible
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

                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Post")
                        }
                    }
                }
            },
            sheetContent = { PreviewBottomSheet() },
            sheetPeekHeight = 0.dp,
            drawerContent = {
                OptionsDrawer(
                    uiState.draftList.values.toList(),
                    clearAll = {
                        viewModel.clearTitleAndContent()
                        scope.launch { scaffoldState.drawerState.close() }
                    },
                    newDraft = { viewModel.createNewDraft() },
                    editHistory = { uiState.currentDraftId?.let { navToEditHistory(it) } },
                )
            },
        ) { innerPadding ->

            Column(
                Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .statusBarsPadding()
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
                    modifier = Modifier.fillMaxWidth(),
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
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(Color.Green)
            ) {

            }
    }

}

@Composable
fun PreviewBottomSheet() {
    Column(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = "hello world")
    }
}


@Composable
fun OptionsDrawer(
    listOfDraft: List<String>,
    clearAll: () -> Unit,
    editHistory: () -> Unit,
    newDraft: () -> Unit,
) {
    Column {
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


        listOfDraft.forEach {
            Text(text = it, modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(15.dp))
            Divider(Modifier.fillMaxWidth())
        }
    }
}
