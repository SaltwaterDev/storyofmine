package com.unlone.app.android.ui.write

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue


@Stable
class WritingScreenState(
    private val bodyText: String,
    private val setBodyText: (String) -> Unit,
) {
    val setBodyTextField: (TextFieldValue) -> Unit = {
        bodyTextField = it
        setBodyText(it.text)
    }

    // Other UI-scoped types
    var bodyTextField: TextFieldValue by mutableStateOf(
        TextFieldValue(
            bodyText,
            TextRange(bodyText.length)
        )
    )

    fun addImageMD(uri: Uri?) {
        uri?.let {
            val imageMD = "![image]($it)"
            setBodyText(bodyText + imageMD)
        }
    }
}


@Composable
fun rememberWritingScreenState(
    bodyText: String,
    setBodyText: (String) -> Unit,
): WritingScreenState =
    remember(bodyText, setBodyText) {
        WritingScreenState(bodyText, setBodyText)
    }