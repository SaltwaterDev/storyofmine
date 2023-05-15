package com.example.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.app.android.ui.MyStoriesApp
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Rule
import org.junit.Test

class MyComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class,
        InternalCoroutinesApi::class
    )
    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            MyStoriesApp()
        }

//        composeTestRule.onNodeWithText("Continue").performClick()
//        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}
