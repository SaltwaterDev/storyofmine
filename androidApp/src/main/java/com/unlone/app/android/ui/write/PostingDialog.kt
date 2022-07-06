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

@Composable
fun PostingDialog(
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
            TopicRow()
            PublishToggleRow(publishState, switchPublish)
            CommentSwitchRow(commentState, publishState, switchComment)
            SavableToggleRow(savableState, publishState, switchSavable)
            ButtonsRow(preview, post)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopicRow() {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            label = { Text("Topic") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        // filter options based on text field value
        val filteringOptions =
            options.filter { it.contains(selectedOptionText, ignoreCase = true) }
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
                            selectedOptionText = selectionOption
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
        Text(text = "Post to Public")
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
        Text(text = "Open comment")
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
        Text(text = "Savable")
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
        Button(onClick = onPreview, modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(text = "Preview")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(onClick = onPost, modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(text = "Post")
        }
    }
}

@Preview
@Composable
fun PostingDialogPreview() {
    PostingDialog({}, false, false, false, {}, {}, {}, {}, {})
}