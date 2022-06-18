package com.unlone.app.android.ui.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.model.SignUpUiEvent
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.auth.AuthResult

@Composable
fun SignUpScreen(
    navToSendEmailOtp: () -> Unit,
    navToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel,
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
//                    navToSendEmailOtp()       todo
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

    Box {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Create an account")
            Spacer(modifier = Modifier.height(35.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (uiState.email.isNotBlank() && !it.isFocused) {
                            viewModel.onEvent(SignUpUiEvent.SignUpEmailVerify)
                        }
                    },
                value = uiState.email,
                label = { Text(text = "Email", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.SignUpEmailChanged(it)) },
                singleLine = true,
                isError = uiState.emailError
            )
            if (uiState.emailError)
                Text(
                    "This email has been used",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            Spacer(Modifier.height(14.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                label = { Text(text = "Password", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.SignUpPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = uiState.pwError
            )
            Text(
                "Your password must contain at least one upper case letter one lower case letter and one number",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(14.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.confirmedPassword,
                label = { Text(text = "Confirm Password", fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.ConfirmedPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = uiState.confirmedPwError
            )

            if (uiState.confirmedPwError)
                Text(
                    "Password and Confirmed password are not the same",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

            Spacer(Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = navToSignIn,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    enabled = true
                ) {
                    Text(text = "Sign In")
                }

                Button(
                    onClick = { viewModel.onEvent(SignUpUiEvent.SignUp) },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = uiState.btnEnabled
                ) {
                    Text(text = "Next")
                }
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