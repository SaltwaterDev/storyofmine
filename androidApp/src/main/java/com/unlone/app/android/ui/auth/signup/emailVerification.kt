package com.unlone.app.android.ui.auth.signup

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unlone.app.android.viewmodel.SignUpUiState

private enum class ScreenState {
    EmailConfirm,
    OtpInput
}

@Composable
fun EmailVerificationScreen(
    state: SignUpUiState,
    onCancelSignUp: () -> Unit,
    setOtp: (String) -> Unit,
    onOtpGenerate: () -> Unit,
    onOtpVerified: () -> Unit,
    navToSetUsername: () -> Unit,
) {
    var showCancelSignupAlert by remember { mutableStateOf(false) }
    var screenState: ScreenState by remember { mutableStateOf(ScreenState.EmailConfirm) }

    BackHandler {
        when (screenState) {
            ScreenState.EmailConfirm -> showCancelSignupAlert = true
            ScreenState.OtpInput -> screenState = ScreenState.EmailConfirm
        }
    }
    Column(
        Modifier
            .statusBarsPadding()
            .displayCutoutPadding()
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        when (screenState) {
            ScreenState.EmailConfirm -> {
                EmailConfirmBlock(
                    Modifier,
                    state.email
                ) {
                    onOtpGenerate()
                    screenState = ScreenState.OtpInput
                }
            }
            ScreenState.OtpInput -> {
                OtpInputBlock(
                    Modifier,
                    state.otp?.toString() ?: "",
                    setOtp,
                ) {
                    onOtpVerified()
                }
            }
        }
    }

    if (showCancelSignupAlert)
        AlertDialog(
            onDismissRequest = { showCancelSignupAlert = false },
            text = { Text(text = "If you go back to last page, you need to sign up again.") },
            confirmButton = {
                TextButton(
                    onClick = { showCancelSignupAlert = false }) {
                    Text(text = "Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCancelSignupAlert = false
                    onCancelSignUp()
                }) {
                    Text(text = "Cancel")
                }
            },
        )

    if (state.verified) {
        LaunchedEffect(Unit) {
            navToSetUsername()
        }
    }
}

@Composable
fun OtpInputBlock(
    modifier: Modifier,
    otp: String,
    onValueChanged: (String) -> Unit,
    onClick: () -> Unit,
) {
    Column(modifier) {
        Text(text = "Enter the code")
        Spacer(modifier = Modifier.height(15.dp))
        TextField(value = otp, onValueChange = onValueChanged, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.align(End)
        ) {
            Text(text = "Confirm")
        }
    }
}

@Composable
fun EmailConfirmBlock(
    modifier: Modifier,
    email: String,
    onClick: () -> Unit,
) {
    Column(modifier) {
        Text(text = "Verify code will be sent to this email")
        Spacer(modifier = Modifier.height(15.dp))
        Surface(
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.align(End)
        ) {
            Text(text = "Send")
        }

    }

}


@Preview
@Composable
private fun Preview() {
    EmailVerificationScreen(SignUpUiState(), {}, {}, {}, {}, {})
}