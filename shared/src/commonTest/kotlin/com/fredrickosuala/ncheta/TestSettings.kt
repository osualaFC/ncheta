package com.fredrickosuala.ncheta

import com.russhwolf.settings.Settings

/**
 * Test implementation of Settings for unit testing
 */
class TestSettings : Settings {
    private val storage = mutableMapOf<String, Any>()
    
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return storage[key] as? Boolean ?: defaultValue
    }
    
    override fun putBoolean(key: String, value: Boolean) {
        storage[key] = value
    }
    
    override fun getString(key: String, defaultValue: String): String {
        return storage[key] as? String ?: defaultValue
    }
    
    override fun putString(key: String, value: String) {
        storage[key] = value
    }
    
    override fun getInt(key: String, defaultValue: Int): Int {
        return storage[key] as? Int ?: defaultValue
    }
    
    override fun putInt(key: String, value: Int) {
        storage[key] = value
    }
    
    override fun getLong(key: String, defaultValue: Long): Long {
        return storage[key] as? Long ?: defaultValue
    }
    
    override fun putLong(key: String, value: Long) {
        storage[key] = value
    }
    
    override fun getDouble(key: String, defaultValue: Double): Double {
        return storage[key] as? Double ?: defaultValue
    }
    
    override fun putDouble(key: String, value: Double) {
        storage[key] = value
    }
    
    override fun getFloat(key: String, defaultValue: Float): Float {
        return storage[key] as? Float ?: defaultValue
    }
    
    override fun putFloat(key: String, value: Float) {
        storage[key] = value
    }
    
    override fun remove(key: String) {
        storage.remove(key)
    }
    
    override fun clear() {
        storage.clear()
    }
    
    override val keys: Set<String>
        get() = storage.keys.toSet()
    
    override val size: Int
        get() = storage.size
    
    override fun hasKey(key: String): Boolean {
        return storage.containsKey(key)
    }
    
    override fun getBooleanOrNull(key: String): Boolean? {
        return storage[key] as? Boolean
    }
    
    override fun getStringOrNull(key: String): String? {
        return storage[key] as? String
    }
    
    override fun getIntOrNull(key: String): Int? {
        return storage[key] as? Int
    }
    
    override fun getLongOrNull(key: String): Long? {
        return storage[key] as? Long
    }
    
    override fun getDoubleOrNull(key: String): Double? {
        return storage[key] as? Double
    }
    
    override fun getFloatOrNull(key: String): Float? {
        return storage[key] as? Float
    }
    
    // Helper method for testing
    fun getStorage(): Map<String, Any> = storage.toMap()
} 