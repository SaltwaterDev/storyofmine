package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.unlone.app.android.R


@Composable
fun PreviewBottomSheet(
    title: String,
    content: String,
    onClose: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Icon(
                painter = painterResource(id = R.drawable.icon_close),
                contentDescription = "close",
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(text = title, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(34.dp))
        RichText(modifier = Modifier.padding(horizontal = 16.dp)) {
            Markdown(content = content.trimIndent())
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
