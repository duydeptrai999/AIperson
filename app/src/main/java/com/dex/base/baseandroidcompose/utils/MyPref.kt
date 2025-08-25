package com.dex.base.baseandroidcompose.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object MyPref {
    private const val PREF_NAME = "app_preferences"
    const val LAST_FETCH_REMOTE_CONFIG_TIME_KEY = "LAST_FETCH_REMOTE_CONFIG_TIME"
    const val TOTAL_TIME_OPEN_APP = "TOTAL_TIME_OPEN_APP"
    const val DISPLAYED_INTRO = "DISPLAYED_INTRO"

    // Get SharedPreferences instance
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // String operations
    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getPreferences(context).getString(key, defaultValue) ?: defaultValue
    }
    
    fun putString(context: Context, key: String, value: String) {
        getPreferences(context).edit { putString(key, value) }
    }
    
    // Int operations
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }
    
    fun putInt(context: Context, key: String, value: Int) {
        getPreferences(context).edit { putInt(key, value) }
    }
    
    // Boolean operations
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }
    
    fun putBoolean(context: Context, key: String, value: Boolean) {
        getPreferences(context).edit { putBoolean(key, value) }
    }
    
    // Long operations
    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return getPreferences(context).getLong(key, defaultValue)
    }
    
    fun putLong(context: Context, key: String, value: Long) {
        getPreferences(context).edit { putLong(key, value) }
    }
    
    // Float operations
    fun getFloat(context: Context, key: String, defaultValue: Float = 0f): Float {
        return getPreferences(context).getFloat(key, defaultValue)
    }
    
    fun setFloat(context: Context, key: String, value: Float) {
        getPreferences(context).edit { putFloat(key, value) }
    }
    
    // Clear specific preference
    fun remove(context: Context, key: String) {
        getPreferences(context).edit { remove(key) }
    }
    
    // Clear all preferences
    fun clearAll(context: Context) {
        getPreferences(context).edit { clear() }
    }
    
    // Check if key exists
    fun contains(context: Context, key: String): Boolean {
        return getPreferences(context).contains(key)
    }
    
    // Additional utility: Get all preferences as a Map
    fun getAll(context: Context): Map<String, *> {
        return getPreferences(context).all
    }
    
    // For storing stringSet
    fun getStringSet(context: Context, key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        return getPreferences(context).getStringSet(key, defaultValue) ?: defaultValue
    }
    
    fun setStringSet(context: Context, key: String, value: Set<String>) {
        getPreferences(context).edit { putStringSet(key, value) }
    }
}