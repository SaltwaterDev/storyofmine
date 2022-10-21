package com.unlone.app.android.ui.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.model.SignUpUiEvent
import com.unlone.app.android.viewmodel.SignUpViewModel
import com.unlone.app.data.auth.AuthResult
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun SignUpScreen(
    navToSendEmailOtp: () -> Unit,
    navToSignIn: () -> Unit,
    viewModel: SignUpViewModel,
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    navToSendEmailOtp()
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(resource = SharedRes.strings.sign_up__title),
                fontSize = 36.sp
            )
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
                label = {
                    Text(
                        text = stringResource(resource = SharedRes.strings.common__email),
                        fontSize = 14.sp
                    )
                },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.SignUpEmailChanged(it)) },
                singleLine = true,
                isError = uiState.emailError
            )
            if (uiState.emailError)
                Text(
                    stringResource(resource = SharedRes.strings.sign_up__email_already_used),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colors.error
                )
            Spacer(Modifier.height(14.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                label = {
                    Text(
                        text = stringResource(resource = SharedRes.strings.common__password),
                        fontSize = 14.sp
                    )
                },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.SignUpPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = uiState.pwError
            )
            Text(
                stringResource(resource = SharedRes.strings.sign_up__pw_tips),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(14.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.confirmedPassword,
                label = {
                    Text(
                        text = stringResource(resource = SharedRes.strings.common__email),
                        fontSize = 14.sp
                    )
                },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.ConfirmedPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = uiState.confirmedPwError
            )

            if (uiState.confirmedPwError)
                Text(
                    stringResource(resource = SharedRes.strings.sign_up__pw_and_confirm_pw_not_same),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.error
                )

            Spacer(Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = navToSignIn,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    enabled = true,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(resource = SharedRes.strings.sign_in__btn_sign_in_instead))
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = { viewModel.onEvent(SignUpUiEvent.SignUp) },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = uiState.btnEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_sign_up))
                    if (uiState.loading)
                        CircularProgressIndicator()
                }
            }

        }
    }

    uiState.errorMsg?.let {
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorMsg() },
            title = { Text(text = "Warning") },
            text = { Text(uiState.errorMsg) },
            confirmButton = {
                Button(onClick = { viewModel.dismissErrorMsg() }) { Text(stringResource(resource = SharedRes.strings.common__btn_confirm)) }
            },
        )
    }
}