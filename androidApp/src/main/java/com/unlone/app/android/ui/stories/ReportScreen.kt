package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.unlone.app.android.viewmodel.ReportViewModel

@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    type: String?,
    reported: String?,
    back: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val reportReasons = state.reportReasons
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .selectableGroup()
    ) {
        reportReasons.forEach { reason ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = ((reason.id == (state.selectedReportReason?.id ?: false))),
                        onClick = {
                            viewModel.onReportOptionSelected(reason)
                            focusManager.clearFocus()
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = ((reason.id == (state.selectedReportReason?.id ?: false))),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = reason.text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }


        // other reason text field
        TextField(
            value = state.otherReportReason,
            onValueChange = viewModel::setOtherReportReason,
            modifier = Modifier
                .onFocusChanged {
                    if (it.isFocused) {
                        viewModel.clearSelectedReason()
                    }
                }
                .focusRequester(focusRequester)
        )

        Button(onClick = {
            if (reported != null && type != null) {
                viewModel.sendReport(type, reported)
            }
        }) {
            Text("Send")
        }
    }


    state.errorMsg?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(text = it, color = Color.Black) },
            confirmButton = {
                Button(onClick = viewModel::dismissError) {

                }
            })
    }

    if (state.reportSuccess) {
        AlertDialog(
            onDismissRequest = back,
            title = { Text(text = "Report Success") },
            confirmButton = {
                Button(onClick = back) {
                    Text(text = "Return")
                }
            }
        )
    }


}
