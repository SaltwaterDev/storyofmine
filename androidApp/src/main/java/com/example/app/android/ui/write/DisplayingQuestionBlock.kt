package com.example.app.android.ui.write

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun DisplayingQuestionBlock(question: String?, modifier: Modifier = Modifier) {
    Crossfade(targetState = question != null, modifier = modifier) { state ->
        if (state) {
            Surface(
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
            ) {
                Text(text = question!!, modifier = Modifier.padding(16.dp, 8.dp))
            }
        }
    }
}