package com.example.myauto.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferencesManager {
    private const val PREFS_APP = "AppPreferences"
    private const val PREFS_USER = "UserPreferences"

    object Keys {
        const val GUEST_MODE = "guestMode"
        const val FUEL_TYPE = "fuelType"
    }

    private lateinit var appPrefs: SharedPreferences
    private lateinit var userPrefs: SharedPreferences

    fun initialize(context: Context) {
        if (!::appPrefs.isInitialized) {
            appPrefs = context.getSharedPreferences(PREFS_APP, Context.MODE_PRIVATE)
        }
        if (!::userPrefs.isInitialized) {
            userPrefs = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE)
        }
    }

    fun setGuestMode(flag: Boolean) = appPrefs.edit { putBoolean(Keys.GUEST_MODE, flag) }
    fun isGuestMode() = appPrefs.getBoolean(Keys.GUEST_MODE, false)

    fun setFuelType(type: Int) = userPrefs.edit { putInt(Keys.FUEL_TYPE, type) }
    fun getFuelType() = userPrefs.getInt(Keys.FUEL_TYPE, -1)

}