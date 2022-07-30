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
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@Composable
fun SetUsernameScreen(
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel,
) {
    val uiState = viewModel.uiState

    if (uiState.success)
        LaunchedEffect(uiState.success) {
            onSignUpSuccess()
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

            Text(
                text = stringResource(resource = SharedRes.strings.set_username__title),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.username,
                label = {
                    Text(
                        text = stringResource(resource = SharedRes.strings.set_username__username),
                        fontSize = 14.sp
                    )
                },
                onValueChange = { viewModel.onEvent(SignUpUiEvent.UsernameChanged(it)) },
                singleLine = true,
            )
            Button(
                onClick = { viewModel.onEvent(SignUpUiEvent.SetUsername) },
                colors = ButtonDefaults.outlinedButtonColors(),
                enabled = uiState.username.isNotBlank(),
                modifier = Modifier.align(End)
            ) {
                Text(text = stringResource(resource = SharedRes.strings.common__btn_finish))
                if (uiState.loading)
                    CircularProgressIndicator()
            }
        }
    }

    uiState.errorMsg?.let {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMsg() },
            title = { Text(text = stringResource(resource = SharedRes.strings.common__warning)) },
            text = { Text(uiState.errorMsg) },
            confirmButton = {
                Button(onClick = { viewModel.dismissMsg() }) { Text(stringResource(resource = SharedRes.strings.common__btn_confirm)) }
            },
        )
    }
}