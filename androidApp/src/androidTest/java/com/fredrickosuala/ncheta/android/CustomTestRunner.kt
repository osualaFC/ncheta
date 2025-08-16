package com.fredrickosuala.ncheta.android

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.mockk

/**
 * Custom test runner for the Ncheta Android app.
 * This runner provides custom configuration for instrumented tests.
 */
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        // Return a mock application for testing
        // This prevents issues with real application initialization during tests
        return mockk<Application>(relaxed = true)
    }

    override fun onStart() {
        super.onStart()
        // Add any additional test setup here
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        // Add any cleanup code here
        super.finish(resultCode, results)
    }
} 