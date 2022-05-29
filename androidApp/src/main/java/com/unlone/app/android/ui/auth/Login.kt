package com.unlone.app.android.ui.auth

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.viewmodel.LoginViewModel
import com.unlone.app.android.R
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navToReg: () -> Unit,
    viewModel: LoginViewModel
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState = viewModel.uiState

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
                value = email,
                label = { Text(text = "Email", fontSize = 14.sp, color = Color.White) },
                onValueChange = { email = it },
                singleLine = true,
            )


            Spacer(Modifier.height(30.dp))

            TextField(
                value = password,
                label = { Text(text = "Password", fontSize = 14.sp, color = Color.White) },
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
            )

            Spacer(Modifier.height(60.dp))

            Row {
                Button(
                    onClick = navToReg,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    enabled = uiState.btnEnabled
                ) {
                    Text(text = "Sign up")
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = { viewModel.performLogin(email, password) },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = uiState.btnEnabled
                ) {
                    Text(text = "Login")
                }
            }
        }
    }


    if (uiState.userExists)
        onLoginSuccess()

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
