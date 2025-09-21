package com.fredrickosuala.ncheta.android.navigation

sealed class Route(val path: String) {
    object Loading : Route("loading")
    object Onboarding : Route("onboarding")
    object Settings : Route("settings/{isFirstTime}") {
        fun create(isFirstTime: Boolean) = "settings/$isFirstTime"
    }
    object Paywall : Route("paywall")
    object Auth : Route("auth")
    object Practice : Route("practice/{entryId}") {
        fun create(entryId: String) = "practice/$entryId"
    }

    // Bottom Nav
    object Create : Route("create")
    object Entries : Route("entries")
}
