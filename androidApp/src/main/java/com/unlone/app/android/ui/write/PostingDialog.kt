package com.unlone.app.android.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun PostingDialog(
    topics: List<String>,
    selectedTopic: String,
    onTopicSelected: (String) -> Unit,
    onCancel: () -> Unit,
    publishState: Boolean,
    commentState: Boolean,
    savableState: Boolean,
    switchPublish: (Boolean) -> Unit,
    switchComment: (Boolean) -> Unit,
    switchSavable: (Boolean) -> Unit,
    preview: () -> Unit,
    post: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(10.dp)
        ) {
            TopicRow(topics, selectedTopic, onTopicSelected)
            PublishToggleRow(publishState, switchPublish)
            CommentSwitchRow(commentState, publishState, switchComment)
            SavableToggleRow(savableState, publishState, switchSavable)
            ButtonsRow(preview, post)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopicRow(
    topics: List<String>,
    selectedTopic: String,
    onTopicSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedTopic,
            onValueChange = {
                onTopicSelected(it)
                expanded = true
            },
            label = { Text( stringResource(resource = SharedRes.strings.writing__topic)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
//            readOnly = expanded
        )
        // filter options based on text field value
        val filteringOptions =
            topics.filter { it.contains(selectedTopic, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            onTopicSelected(selectionOption)
                            expanded = false
                        }
                    ) {
                        Text(text = selectionOption)
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishToggleRow(
    checkedState: Boolean,
    switchPublish: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(resource = SharedRes.strings.writing__post_to_public))
        Switch(
            checked = checkedState,
            onCheckedChange = switchPublish
        )
    }
}

@Composable
private fun CommentSwitchRow(
    checkedState: Boolean,
    enabled: Boolean,
    switchComment: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(resource = SharedRes.strings.writing__open_comment))
        Switch(
            checked = checkedState,
            onCheckedChange = switchComment,
            enabled = enabled
        )
    }
}

@Composable
private fun SavableToggleRow(
    checkedState: Boolean,
    enabled: Boolean,
    switchSave: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(resource = SharedRes.strings.writing__savable))
        Switch(
            checked = checkedState,
            onCheckedChange = switchSave,
            enabled = enabled
        )
    }
}

@Composable
private fun ButtonsRow(
    onPreview: () -> Unit,
    onPost: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onPreview, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = stringResource(resource = SharedRes.strings.writing__preview))
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(
            onClick = onPost, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = stringResource(resource = SharedRes.strings.writing__btn_post))
        }
    }
}

@Preview
@Composable
fun PostingDialogPreview() {
    PostingDialog(listOf(), "Topic 2", {}, {}, false, false, false, {}, {}, {}, {}, {})
}