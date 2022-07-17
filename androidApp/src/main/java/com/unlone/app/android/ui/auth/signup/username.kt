package com.unlone.app.android.ui.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.model.SignUpUiEvent
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.data.auth.AuthResult


@Composable
fun SetUsernameScreen(
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel,
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onSignUpSuccess()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, result.errorMsg, Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "unknown error: " + result.errorMsg,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "You are verified! Please Enter your username", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.username,
                label = { Text(text = "Username", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.UsernameChanged(it)) },
                singleLine = true,
            )
            Button(
                onClick = { viewModel.onEvent(SignUpUiEvent.SetUsername) },
                colors = ButtonDefaults.outlinedButtonColors(),
                enabled = uiState.username.isNotBlank(),
                modifier = Modifier.align(End)
            ) {
                Text(text = "Finish")
                if (uiState.loading)
                    CircularProgressIndicator()
            }

        }
    }

    uiState.errorMsg?.let {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMsg() },
            title = { Text(text = "Warning") },
            text = { Text(uiState.errorMsg) },
            confirmButton = {
                Button(onClick = { viewModel.dismissMsg() }) { Text("Confirm") }
            },
        )
    }
}