package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.data.story.SimpleStory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Post(
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
        elevation = 0.dp,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(Modifier) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp, 20.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 2,
            )
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                maxLines = 5,
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}




@Preview
@Composable
fun PostPreview() {
    Post("_title", "_content", false) {}
}