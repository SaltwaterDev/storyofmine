package com.unlone.app.android.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.model.AuthUiEvent
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.auth.AuthResult
import com.unlone.app.kermit

@Composable
fun SignUpEmailScreen(
    onEmailConfirmed: () -> Unit,
    viewModel: SignUpViewModel,
    navToSignIn: () -> Unit
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    kermit.d { "ok la" }
                    onEmailConfirmed()
                }
                is AuthResult.Unauthorized -> {
                    kermit.d { "not ok" }
                    Toast.makeText(context, result.errorMsg, Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "unknown error: " + result.data.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = uiState.email,
                label = { Text(text = "Email", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignUpEmailChanged(it)) },
                singleLine = true,
            )

            Spacer(Modifier.height(60.dp))

            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.SignUpEmailVerify) },
                colors = ButtonDefaults.buttonColors(),
                enabled = uiState.emailBtnEnabled
            ) {
                Text(text = "Continue")
            }

            Row {
                Text(text = "Already have an account?")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sign In here", modifier = Modifier.clickable { navToSignIn() })
            }

            if (uiState.loading)
                CircularProgressIndicator()
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