package com.unlone.app.android.ui.write

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleLarge
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun WrittenContentBlock(
    innerPadding: PaddingValues,
    loading: Boolean,
    title: String,
    setTitle: (String) -> Unit,
    bodyTextField: TextFieldValue,
    setBodyTextField: (TextFieldValue) -> Unit,
    bottomPadding: Dp,
) {
    Column(
        Modifier.padding(innerPadding)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    visible = loading,
                    highlight = PlaceholderHighlight.fade()
                ),
            value = title,
            onValueChange = setTitle,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            placeholder = {
                Text(text = stringResource(resource = SharedRes.strings.writing__placeholder))
            },
            textStyle = Typography.titleLarge,
            readOnly = loading,
        )

        TextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding)
                .placeholder(
                    visible = loading, highlight = PlaceholderHighlight.fade()
                ),
            value = bodyTextField,
            onValueChange = setBodyTextField,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            textStyle = Typography.body1,
            readOnly = loading,
        )
    }
}