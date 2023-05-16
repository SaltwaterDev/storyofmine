package com.example.app.android.ui.comonComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.android.ui.theme.Typography
import com.example.app.android.ui.theme.titleMedium

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoryCard(
    title: String,
    content: String,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = !loading,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
//        elevation = if (isSystemInDarkTheme()) 4.dp else 0.dp,
    ) {

        Column(Modifier) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp, 20.dp),
                style = Typography.titleMedium,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis
            )

            var bodyLineCount = 1
            Text(
                text = content + "\n".repeat((bodyMaxLines - bodyLineCount)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                maxLines = bodyMaxLines,
                style = Typography.body2,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    bodyLineCount = textLayoutResult.lineCount
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}
const val titleMaxLines = 2
const val bodyMaxLines = 4

@Preview
@Composable
fun StoryCardPreview() {
    StoryCard("_title", "_content", false) {}
}