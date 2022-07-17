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
            Text(text = "Options", modifier = Modifier.padding(16.dp), style = Typography.h1)
            BlockWithIcon(R.drawable.ic_clear, "Clear") { clearAll() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_history, "Edit History") { editHistory() }
            Divider(Modifier.fillMaxWidth())
            BlockWithIcon(R.drawable.ic_add, "New Draft") { newDraft() }
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
        Text(text = title, modifier = Modifier.padding(16.dp))
    }
}
