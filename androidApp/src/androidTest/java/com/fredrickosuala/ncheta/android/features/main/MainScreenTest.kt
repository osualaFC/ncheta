package com.fredrickosuala.ncheta.android.features.main

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fredrickosuala.ncheta.features.main.MainViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainScreenShowsLoadingInitially() {
        // Arrange
        val mockViewModel = mockk<MainViewModel>()
        every { mockViewModel.hasCompletedOnboarding } returns MutableStateFlow(null)
        every { mockViewModel.isReadyToUseApp } returns MutableStateFlow(false)

        // Act
        composeTestRule.setContent {
            MainScreen(mainViewModel = mockViewModel)
        }

        // Assert
        composeTestRule.onNodeWithTag("CircularProgressIndicator").assertExists()
    }

    @Test
    fun testMainScreenShowsOnboardingWhenNotCompleted() {
        // Arrange
        val mockViewModel = mockk<MainViewModel>()
        every { mockViewModel.hasCompletedOnboarding } returns MutableStateFlow(false)
        every { mockViewModel.isReadyToUseApp } returns MutableStateFlow(false)
        every { mockViewModel.setOnboardingComplete() } just Runs

        // Act
        composeTestRule.setContent {
            MainScreen(mainViewModel = mockViewModel)
        }

        // Assert
        // The onboarding screen should be displayed
        // Note: We can't test the exact content without the actual OnboardingScreen implementation
        // but we can verify that the loading indicator is not shown
        composeTestRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun testMainScreenShowsSettingsWhenOnboardingCompleteButNoApiKey() {
        // Arrange
        val mockViewModel = mockk<MainViewModel>()
        every { mockViewModel.hasCompletedOnboarding } returns MutableStateFlow(true)
        every { mockViewModel.isReadyToUseApp } returns MutableStateFlow(false)

        // Act
        composeTestRule.setContent {
            MainScreen(mainViewModel = mockViewModel)
        }

        // Assert
        // The settings screen should be displayed
        composeTestRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun testMainScreenShowsMainContentWhenReady() {
        // Arrange
        val mockViewModel = mockk<MainViewModel>()
        every { mockViewModel.hasCompletedOnboarding } returns MutableStateFlow(true)
        every { mockViewModel.isReadyToUseApp } returns MutableStateFlow(true)

        // Act
        composeTestRule.setContent {
            MainScreen(mainViewModel = mockViewModel)
        }

        // Assert
        // The main content should be displayed
        composeTestRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }
} 