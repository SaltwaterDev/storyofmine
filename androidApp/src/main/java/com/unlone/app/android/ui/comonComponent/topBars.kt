package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.unlone.app.android.R

@Composable
fun WriteScreenTopBar(
    openOptions: () -> Unit,
    openPreview: () -> Unit,
    post: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = openOptions) {
            Icon(painterResource(id = R.drawable.ic_menu), contentDescription = "options")
        }
        TextButton(onClick = openPreview) {
            Text(text = "Preview")
        }
        IconButton(onClick = post) {
            Icon(painterResource(id = R.drawable.ic_send), contentDescription = "post")
        }
    }
}

@Composable
fun WriteScreenTopBarPreview() {
    WriteScreenTopBar({}, {}, {})
}
