package com.unlone.app.android.ui.write

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun RequireSignInDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(resource = SharedRes.strings.writing__sign_in_required_title)) },
        text = { Text(text = stringResource(resource = SharedRes.strings.writing__sign_in_required_text)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
            ) {
                Text(text = stringResource(resource = SharedRes.strings.sign_in__btn_sign_in))

            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) { Text(text = stringResource(resource = SharedRes.strings.common__btn_cancel)) }
        },
    )
}