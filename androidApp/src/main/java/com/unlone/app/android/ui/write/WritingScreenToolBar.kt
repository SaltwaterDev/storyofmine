package com.unlone.app.android.ui.write

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unlone.app.android.R


@Composable
fun WritingScreenToolBar(
    inputImage: () -> Unit,
    fetGuidingQuestion: () -> Unit,
) {
    Row(
        Modifier
            .height(imeToolBarHeight.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Green)
    ) {
        IconButton(onClick = {
            inputImage()
        }) {
            Icon(
                painterResource(id = R.drawable.image),
                contentDescription = "input image"
            )
        }
        IconButton(onClick = { fetGuidingQuestion() }) {
            Icon(
                Icons.Rounded.Lightbulb,
                contentDescription = "get guiding question"
            )
        }
    }
}