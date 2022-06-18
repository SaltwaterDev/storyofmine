package com.unlone.app.android.ui.write

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.unlone.app.android.viewmodel.WritingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel
) {
    val viewModelState = viewModel.state
    val context = LocalContext.current

    // when leaving the page, save the current draft
    DisposableEffect(Unit){
        onDispose {
            viewModel.saveDraft()
        }
    }

    // todo: store the draft when leaving the page at local db
    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()


    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val systemUiController: SystemUiController = rememberSystemUiController()

    DisposableEffect(key1 = context) {
        systemUiController.isSystemBarsVisible = false
        onDispose {
            systemUiController.isSystemBarsVisible = true
        }

    }
    // todo: when keyboard is opened, hide the system bar + bottom bar
    /*LaunchedEffect(key1 = isKeyboardVisible) {
        Timber.d("hello ")
        systemUiController.isSystemBarsVisible = !isKeyboardVisible
    }*/
    systemUiController.isSystemBarsVisible = false
    BottomSheetScaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar() {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { scope.launch { if (scaffoldState.bottomSheetState.isCollapsed) scaffoldState.bottomSheetState.expand() else scaffoldState.bottomSheetState.collapse() } }) {
                        Text(text = "Preview")
                    }
                    Button(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Text(text = "Options")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Post")
                    }
                }
            }
        },
        sheetContent = {
            PreviewBottomSheet()
        },
        sheetPeekHeight = 0.dp,
        drawerContent = {
            OptionsDrawer(
                viewModelState.draftList,
                clearAll = {
                    viewModel.clearTitleAndContent()
                    scope.launch { scaffoldState.drawerState.close() }
                })
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModelState.title,
                onValueChange = { viewModel.setTitle(it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                value = viewModelState.content,
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
fun OptionsDrawer(listOfDraft: List<String>, clearAll: () -> Unit) {
    Column {
        Text(text = "Clear", modifier = Modifier
            .fillMaxWidth()
            .clickable { clearAll() }
            .padding(15.dp))
        Divider(Modifier.fillMaxWidth())
        Text(text = "Edit History", modifier = Modifier
            .fillMaxWidth()
            .clickable { }
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