package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.ui.theme.topicTableElement
import com.unlone.app.data.story.Topic
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun TopicTable(
    modifier: Modifier,
    topics: List<Topic>,
    onTopicClick: (String) -> Unit,
    viewMoreTopic: () -> Unit
) {
    Column(modifier) {
        Row(Modifier.padding(bottom = 4.dp)) {
            Text(
                text = stringResource(resource = SharedRes.strings.common__topic),
                modifier = Modifier.weight(1f),
                style = Typography.subtitle2
            )
            Text(
                text = stringResource(resource = SharedRes.strings.stories_show_more),
                modifier = Modifier
                    .clickable { viewMoreTopic() }
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                fontSize = 13.sp,
            )
        }
        Divider(color = Color.Black)
        Spacer(modifier = Modifier.height(3.dp))
        topics.forEach { TopicItemRow(topic = it.name, onTopicClick = { onTopicClick(it.name) }) }
    }
}

@Composable
fun TopicItemRow(topic: String, onTopicClick: () -> Unit) {
    Row(Modifier.clickable { onTopicClick() }) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(text = topic, style = Typography.topicTableElement, modifier = Modifier.padding(vertical = 8.dp))
            Divider(color = Color.Black, thickness = 0.5.dp)
        }
    }
}


@Preview
@Composable
fun TopicTablePreview() {
    TopicTable(
        Modifier,
        List(4) {
            Topic(
                id = "123f",
                name = "Topic $it"
            )
        },
        {}, {})
}