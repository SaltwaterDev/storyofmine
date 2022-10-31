package com.unlone.app.android.ui.stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.unlone.app.android.ui.comonComponent.StandardTopBar
import com.unlone.app.android.ui.theme.Typography
import com.unlone.app.android.viewmodel.ReportViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes
import kotlin.text.Typography

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

    val errorDialogShow: MutableState<String?> = remember {
        mutableStateOf(null)
    }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val showConfirmedDialog by remember { mutableStateOf(false) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .selectableGroup()
    ) {

        StandardTopBar(
            stringResource(resource = SharedRes.strings.common__report),
            Modifier,
            back,
        )

        Text(
            text = stringResource(resource = SharedRes.strings.report__title),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            style = Typography.h5
        )

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
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = state.selectedOtherReportReason,
                    onClick = viewModel::onOtherReportSelected,
                    role = Role.RadioButton
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = state.selectedOtherReportReason,
                onClick = null
            )
            TextField(
                value = state.otherReportReason,
                onValueChange = viewModel::setOtherReportReason,
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) viewModel.onOtherReportSelected()
                    }
                    .focusRequester(focusRequester)
                    .padding(start = 16.dp)
            )
        }


        Button(
            modifier = Modifier
                .align(CenterHorizontally)
                .fillMaxWidth()
                .padding(30.dp),
            onClick = {
                if (state.selectedReportReason != null || state.otherReportReason.isNotBlank()) {
                    showConfirmDialog = true
                } else {
                    // fixme
//                    errorDialogShow.value = stringResource(resource = SharedRes.strings.report__no_reason_warning)
                }
            }) {
            Text(stringResource(resource = SharedRes.strings.common__btn_submit))
        }
    }


    state.errorMsg?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(text = it, color = Color.Black) },
            confirmButton = {
                Button(onClick = viewModel::dismissError) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            })
    }

    errorDialogShow.value?.let {
        AlertDialog(
            onDismissRequest = { errorDialogShow.value = null },
            title = { Text(text = it, color = Color.Black) },
            confirmButton = {
                Button(onClick = { errorDialogShow.value = null }) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            })
    }

    if (showConfirmDialog) {
        AlertDialog(
            text = { Text(text = "You are going to report with reason '${state.selectedReportReason?.text ?: state.otherReportReason}'.") },
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                Button(onClick = { viewModel.sendReport(type!!, reported!!) }) {
                    Text(stringResource(resource = SharedRes.strings.common__btn_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text(text = stringResource(resource = SharedRes.strings.common__btn_cancel))
                }
            }
        )
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
