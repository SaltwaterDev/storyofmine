package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.unlone.app.data.story.SimpleStory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Post(title: String, content: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(15.dp, 5.dp),
        elevation = 3.dp
    ) {
        Column() {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                modifier = Modifier.padding(18.dp, 20.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 2,
            )
            Text(
                text = content,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                maxLines = 5,
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}


@Composable
fun HorizontalScrollPosts(
    modifier: Modifier,
    posts: List<SimpleStory>,
    onPostClick: (String) -> Unit
) {
    Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-15).dp)
        ) {

            posts.forEach {
                Post(
                    it.title,
                    it.content,
                    modifier
                ) { onPostClick(it.id) }
            }
        }
    }
}


@Preview
@Composable
fun PostPreview() {
    Post("_title", "_content") {}
}