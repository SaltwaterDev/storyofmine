package com.unlone.app.android.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.model.AuthUiEvent
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.auth.AuthResult

@Composable
fun SignUpPwScreen(
    onSignupSuccess: () -> Unit,
    viewModel: SignUpViewModel,
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onSignupSuccess()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, result.errorMsg, Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "unknown error: ${result.errorMsg}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = uiState.password,
                label = { Text(text = "Password", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignUpPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
            )

            Spacer(Modifier.height(10.dp))

            Text("Your password must contain at least one upper case letter one lower case letter and one number")

            TextField(
                value = uiState.confirmedPassword,
                label = { Text(text = "Confirm Password", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(AuthUiEvent.ConfirmedPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
            )

            Spacer(Modifier.height(60.dp))

            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.SignUp) },
                colors = ButtonDefaults.buttonColors(),
                enabled = uiState.signUpBtnEnabled
            ) {
                Text(text = "SignUp")
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
                Button(
                    onClick = { viewModel.dismissMsg() }
                ) {
                    Text("This is the Confirm Button")
                }
            },
        )
    }
}