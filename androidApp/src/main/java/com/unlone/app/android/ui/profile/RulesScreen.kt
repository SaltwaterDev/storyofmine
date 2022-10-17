package com.unlone.app.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unlone.app.android.ui.comonComponent.StandardTopBar
import com.unlone.app.android.ui.theme.Typography
import dev.icerock.moko.resources.compose.stringResource
import org.example.library.SharedRes

@Composable
fun RulesScreen(
    back: () -> Unit
) {
    Scaffold(
        topBar = {
            StandardTopBar(title = stringResource(SharedRes.strings.rules_title)) {
                back()
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp, 30.dp)
        ) {

            repeat(10) {
                Text(text = "You are on 9", style = Typography.h4)
                Text(text = "Why you are on 9", style = Typography.body1)
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}