package com.unlone.app.android.ui.comonComponent

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun NoNetworkScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 60.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.WifiOff,
                contentDescription = null,
                modifier = Modifier.requiredSize(180.dp)
            )
            Text(
                text = stringResource(resource = SharedRes.strings.common__network_unavailable_warning),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}


@Preview
@Composable
fun NoNetworkScreenPreview() {
    NoNetworkScreen()
}
