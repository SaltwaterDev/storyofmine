package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.titleMedium

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
        elevation = 4.dp,
    ) {
        Column(Modifier) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp, 20.dp),
                style = Typography.titleMedium,
                maxLines = 2,
            )
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                maxLines = 5,
                style = Typography.body2
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}




@Preview
@Composable
fun StoryCardPreview() {
    StoryCard("_title", "_content", false) {}
}