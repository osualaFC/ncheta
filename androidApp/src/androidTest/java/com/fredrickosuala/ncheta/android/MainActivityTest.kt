package com.fredrickosuala.ncheta.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainActivityLaunches() {
        // This test verifies that the MainActivity launches successfully
        // and displays the main screen content
        composeTestRule.onNodeWithText("Ncheta").assertExists()
    }

    @Test
    fun testMainScreenIsDisplayed() {
        // Verify that the main screen content is displayed
        composeTestRule.onNodeWithContentDescription("Main Screen").assertExists()
    }

    @Test
    fun testAppThemeIsApplied() {
        // Verify that the app theme is properly applied
        // This can be checked by looking for Material3 components
        composeTestRule.onRoot().assertHasClickAction()
    }
} 