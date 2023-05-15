package com.unlone.app.android.ui.write

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun WritingScreenToolBar(
    modifier: Modifier,
    inputImage: () -> Unit,
    getGuidingQuestion: () -> Unit,
) {
    Surface(Modifier) {
        Row(
            modifier.fillMaxWidth()
        ) {
//        IconButton(onClick = {
//            inputImage()
//        }) {
//            Icon(
//                painterResource(id = R.drawable.image),
//                contentDescription = "input image"
//            )
//        }
            IconButton(onClick = getGuidingQuestion) {
                Icon(
                    Icons.Rounded.Lightbulb,
                    contentDescription = "get guiding question"
                )
            }
        }
    }
}