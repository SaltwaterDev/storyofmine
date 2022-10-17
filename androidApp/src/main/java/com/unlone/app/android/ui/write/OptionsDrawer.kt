package com.unlone.app.android.ui.write

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unlone.app.android.R
import com.unlone.app.android.ui.theme.Typography
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes


@Composable
fun OptionsDrawer(
    listOfDraft: Map<String, String>,
    clearAll: () -> Unit,
    editHistory: () -> Unit,
    newDraft: () -> Unit,
    switchDraft: (String) -> Unit,
) {
    Column {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(text = stringResource(resource = SharedRes.strings.writing__option_drawer_title), modifier = Modifier.padding(16.dp), style = Typography.h6)
            BlockWithIcon(R.drawable.ic_clear,  stringResource(resource = SharedRes.strings.writing__clear)) { clearAll() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_history,  stringResource(resource = SharedRes.strings.writing__edit_history)) { editHistory() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_add,  stringResource(resource = SharedRes.strings.writing__new_draft)) { newDraft() }
            Divider(Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(60.dp))
            listOfDraft.entries.forEach {
                BlockWithIcon(iconId = R.drawable.ic_write, title = it.value) {
                    switchDraft(it.key)
                }
                Divider(Modifier.fillMaxWidth())
            }
        }

    }
}

@Composable
private fun BlockWithIcon(iconId: Int?, title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        iconId?.let {
            Icon(
                painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = title, modifier = Modifier.padding(16.dp), style = Typography.subtitle2)
    }
}
