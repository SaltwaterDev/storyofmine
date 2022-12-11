package com.unlone.app.android.ui.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.unlone.app.android.ui.comonComponent.StoryCard
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.data.story.SimpleStory
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@Composable
fun PostsByTopic(
    topic: String,
    loading: Boolean,
    stories: List<SimpleStory>,
    viewMorePost: () -> Unit,
    navToPostDetail: (String) -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = topic,
                style = Typography.subtitle2,
                modifier = Modifier
                    .weight(1f, false)
                    .placeholder(
                        visible = loading,
                        highlight = PlaceholderHighlight.fade()
                    )
            )

            if (!loading)
                Text(
                    text = stringResource(resource = SharedRes.strings.stories_show_more),
                    modifier = Modifier
                        .clickable { viewMorePost() }
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                    fontSize = 13.sp,
                )
        }
        Spacer(modifier = Modifier.height(7.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stories) {
                StoryCard(
                    it.title,
                    it.content,
                    loading,
                    Modifier
                        .fillParentMaxWidth()
                        .placeholder(
                            visible = loading,
                            highlight = PlaceholderHighlight.fade()
                        )
                ) { navToPostDetail(it.id) }
            }
        }
    }
}

