package com.unlone.app.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unlone.app.viewmodel.WritingViewModel
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun WritingScreen(
    viewModel: WritingViewModel
) {
    val viewModelState = viewModel.state

    // todo: when focus on text field, hide the system bar
    // todo: store the draft when leave the page --> use room db
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar() {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Preview")
                    }
                    Button(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Text(text = "Option")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Post")
                    }
                }
            }
        },
        drawerContent = {
            OptionDrawer(
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
fun OptionDrawer(listOfDraft: List<String>, clearAll: () -> Unit) {
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