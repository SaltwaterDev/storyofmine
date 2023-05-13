package com.unlone.app.android.ui.write

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.unlone.app.data.story.PublishStoryException
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun PostStoryErrorAlert(exception: PublishStoryException, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(text = stringResource(resource = SharedRes.strings.common__oops)) },
        text = { Text(text = stringResource(resource = getPostStoryErrorMessage(exception))) },
        confirmButton = {
            Button(
                onClick = dismiss
            ) {
                Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
            }
        })
}