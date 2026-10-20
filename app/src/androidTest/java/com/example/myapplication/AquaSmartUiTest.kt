package com.example.myapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/** Smoke tests for the native prototype. Run on an emulator or connected device. */
class AquaSmartUiTest {
    @get:Rule val app = createAndroidComposeRule<MainActivity>()

    private fun enter() {
        app.onNodeWithText("Get Started").performScrollTo().performClick()
    }

    @Test
    fun bottomNavigationReachesMainScreens() {
        enter()
        app.onNodeWithText("Monitor").performClick()
        app.onNodeWithText("Water Quality").assertIsDisplayed()
        app.onNodeWithText("Feed").performClick()
        app.onNodeWithText("Fish Feeding").assertIsDisplayed()
        app.onNodeWithText("Analytics").performClick()
        app.onNodeWithText("Predictive Analytics").assertIsDisplayed()
        app.onNodeWithText("Home").performClick()
        app.onNodeWithText("Real-Time Parameters").assertIsDisplayed()
    }

    @Test
    fun alertsFilterExcludesOtherCategories() {
        enter()
        app.onNodeWithText("More").performClick()
        app.onNodeWithText("Alerts & Notifications").performClick()
        app.onNode(hasText("System") and hasClickAction()).performClick()
        app.onNodeWithText("IoT Sensor Online").assertIsDisplayed()
        app.onNodeWithText("High pH Level").assertDoesNotExist()
    }

    @Test
    fun scannerCompletesAndCanReset() {
        enter()
        app.onNodeWithText("More").performClick()
        app.onNodeWithText("Tester Kit Scanner").performClick()
        app.onNodeWithText("📷 Scan Demo Sample").performScrollTo().performClick()
        app.waitUntil(5000) {
            app.onAllNodesWithText("Demo Scan Complete!").fetchSemanticsNodes().isNotEmpty()
        }
        app.onNodeWithText("↻ Scan Again").performScrollTo().performClick()
        app.onNodeWithText("Scan Water Test Kit").assertExists()
    }

    @Test
    fun feederCannotMoveOrFeedWhileDisconnected() {
        enter()
        app.onNodeWithText("Feed").performClick()
        app.onNodeWithText("Feed Now").performScrollTo().assertIsNotEnabled()
        app.onNodeWithText("STOP FEEDER").performScrollTo().assertIsNotEnabled()
    }
}
