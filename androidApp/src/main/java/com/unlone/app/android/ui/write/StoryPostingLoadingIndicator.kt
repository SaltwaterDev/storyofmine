package com.unlone.app.android.ui.write

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun StoryPostingLoadingIndicator(modifier: Modifier) {
    Card(
        modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(Modifier.padding(8.dp)) {
            Text(
                text = stringResource(resource = SharedRes.strings.writing__posting),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            CircularProgressIndicator(Modifier.padding(start = 4.dp))
        }
    }
}