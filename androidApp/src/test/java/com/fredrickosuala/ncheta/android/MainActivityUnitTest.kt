package com.fredrickosuala.ncheta.android

import androidx.activity.ComponentActivity
import org.junit.Test
import org.junit.Assert.*

class MainActivityUnitTest {

    @Test
    fun testMainActivityPackageName() {
        // Test that the MainActivity has the correct package name
        val expectedPackageName = "com.fredrickosuala.ncheta.android"
        assertEquals(expectedPackageName, MainActivity::class.java.packageName)
    }

    @Test
    fun testMainActivityInheritance() {
        // Test that MainActivity extends ComponentActivity
        assertTrue(ComponentActivity::class.java.isAssignableFrom(MainActivity::class.java))
    }

    @Test
    fun testMainActivityHasRequiredMethods() {
        // Test that MainActivity has the required methods
        val methods = MainActivity::class.java.methods
        val onCreateMethod = methods.find { it.name == "onCreate" }
        assertNotNull("MainActivity should have onCreate method", onCreateMethod)
    }
} 