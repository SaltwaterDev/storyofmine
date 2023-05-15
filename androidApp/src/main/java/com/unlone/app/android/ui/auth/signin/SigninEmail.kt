package com.unlone.app.android.ui.auth.signin

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.R
import com.unlone.app.android.model.SignInUiEvent
import com.unlone.app.android.viewmodel.SignInViewModel
import com.unlone.app.data.auth.AuthResult
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import org.example.library.SharedRes

@InternalCoroutinesApi
@Composable
fun SignInEmailScreen(
    back: () -> Unit,
    navToSignInPw: () -> Unit,
    navToSignUp: () -> Unit,
    viewModel: SignInViewModel,
) {

    val uiState = viewModel.uiState

    val context = LocalContext.current


    val unknownErrorText = stringResource(resource = SharedRes.strings.common__unknown_error)
    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    navToSignInPw()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, result.errorMsg, Toast.LENGTH_LONG).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(context, unknownErrorText, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
        IconButton(onClick = { back() }) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "back")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {

            Spacer(modifier = Modifier.height(60.dp))
            Text(text = stringResource(SharedRes.strings.sign_in__title), fontSize = 36.sp)
            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = uiState.email,
                label = {
                    Text(
                        text = stringResource(SharedRes.strings.common__email),
                        fontSize = 14.sp
                    )
                },
                onValueChange = { viewModel.onEvent(SignInUiEvent.SignInEmailChanged(it)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(SignInUiEvent.SignInEmail) }
                )

            )
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { viewModel.onEvent(SignInUiEvent.SignInEmail) },
                colors = ButtonDefaults.buttonColors(),
                enabled = uiState.emailBtnEnabled,
                modifier = Modifier.align(End)
            ) {
                Row(verticalAlignment = CenterVertically) {
                    Text(text = stringResource(SharedRes.strings.common__btn_next))
                    AnimatedVisibility(
                        visible = uiState.loading,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        CircularProgressIndicator()
                    }
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
                Text(text = stringResource(SharedRes.strings.common__warning))
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
