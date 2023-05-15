package com.unlone.app.android.ui.write

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun NetworkUnavailableAlert(dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(text = stringResource(resource = SharedRes.strings.common__oops)) },
        text = { Text(text = stringResource(resource = SharedRes.strings.common__network_unavailable_warning)) },
        confirmButton = {
            Button(onClick = dismiss) {
                Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
            }
        }
    )
}