package com.unlone.app.android.ui.auth.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
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
import com.unlone.app.android.model.SignInUiEvent
import com.unlone.app.data.auth.AuthResult
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import org.example.library.SharedRes

@InternalCoroutinesApi
@Composable
fun SignInPasswordScreen(
    onSignInSuccess: () -> Unit,
    back: () -> Unit,
    viewModel: SignInViewModel
) {

    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onSignInSuccess()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, result.errorMsg, Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = stringResource(resource = SharedRes.strings.sign_in__title), fontSize = 36.sp)
            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = uiState.password,
                label = { Text(text = stringResource(resource = SharedRes.strings.common__password), fontSize = 14.sp) },
                onValueChange = { viewModel.onEvent(SignInUiEvent.SignInPasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { viewModel.onEvent(SignInUiEvent.SignInPw) },
                colors = ButtonDefaults.buttonColors(),
                enabled = uiState.pwBtnEnabled,
                modifier = Modifier.align(End)
            ) {
                Text(text = stringResource(resource = SharedRes.strings.sign_in__btn_sign_in))
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
                    Text(stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            },
        )
    }
}
