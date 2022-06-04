package com.unlone.app.android.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.android.R
import com.unlone.app.android.model.AuthUiEvent
import com.unlone.app.auth.AuthResult
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Composable
fun SignInScreen(
    onLoginSuccess: () -> Unit,
    navToSignUp: () -> Unit,
    viewModel: SignInViewModel
) {

    val uiState = viewModel.uiState

    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onLoginSuccess()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, "You are not authorized", Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(BiasAlignment(0f, -0.3f)),
            horizontalAlignment = CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .fillMaxWidth(0.6f)
                    .aspectRatio(5 / 3f),
                contentScale = ContentScale.Inside
            )

            TextField(
                value = uiState.email,
                label = { Text(text = "Email", fontSize = 14.sp, color = Color.White) },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignInEmailChanged(it)) },
                singleLine = true,
            )


            Spacer(Modifier.height(30.dp))

            TextField(
                value = uiState.password,
                label = { Text(text = "Password", fontSize = 14.sp, color = Color.White) },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignInPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
            )

            Spacer(Modifier.height(60.dp))

            Row {
                Button(
                    onClick = navToSignUp,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    enabled = uiState.btnEnabled
                ) {
                    Text(text = "Sign up")
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = { viewModel.onEvent(AuthUiEvent.SignIn) },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = uiState.btnEnabled
                ) {
                    Text(text = "Login")
                }
            }
        }
    }

    uiState.errorMsg?.let {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissMsg()
            },
            title = {
                Text(text = "Warning")
            },
            text = {
                Text(uiState.errorMsg)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissMsg()
                    }) {
                    Text("This is the Confirm Button")
                }
            },
        )
    }
}
