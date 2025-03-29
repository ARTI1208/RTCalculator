@file:Suppress("unused")

package ru.art2000.extensions.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

fun SharedPreferences.stringPreference(key: String, defaultValue: String) =
    toAppPreferences().stringPreference(key, defaultValue)

fun SharedPreferences.nullableStringPreference(key: String, defaultValue: String? = null) =
    toAppPreferences().nullableStringPreference(key, defaultValue)

fun SharedPreferences.intPreference(key: String, defaultValue: Int) =
    toAppPreferences().intPreference(key, defaultValue)

fun SharedPreferences.longPreference(key: String, defaultValue: Long) =
    toAppPreferences().longPreference(key, defaultValue)

fun SharedPreferences.booleanPreference(key: String, defaultValue: Boolean) =
    toAppPreferences().booleanPreference(key, defaultValue)

fun SharedPreferences.doublePreference(key: String, defaultValue: Double) =
    toAppPreferences().doublePreference(key, defaultValue)

fun SharedPreferences.toAppPreferences(): AppPreferences = AndroidAppPreferences(this)

fun Context.getDefaultAppPreferences() =
    PreferenceManager.getDefaultSharedPreferences(this).toAppPreferences()

class AndroidAppPreferences internal constructor(val sharedPreferences: SharedPreferences) : AppPreferences {

    override fun getString(key: String, defaultValue: String) = sharedPreferences.getString(key, defaultValue)!!

    override fun getNullableString(key: String, defaultValue: String?) = sharedPreferences.getString(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long) = sharedPreferences.getLong(key, defaultValue)

    override fun getDouble(key: String, defaultValue: Double): Double {
        val defaultLong = doubleToLong(defaultValue)
        return longToDouble(sharedPreferences.getLong(key, defaultLong))
    }

    override fun getBoolean(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)

    override fun putNullableString(key: String, value: String?) = sharedPreferences.edit {
        putString(key, value)
    }

    override fun putInt(key: String, value: Int) = sharedPreferences.edit {
        putInt(key, value)
    }

    override fun putLong(key: String, value: Long) = sharedPreferences.edit {
        putLong(key, value)
    }

    override fun putDouble(key: String, value: Double) = sharedPreferences.edit {
        putLong(key, doubleToLong(value))
    }

    override fun putBoolean(key: String, value: Boolean) = sharedPreferences.edit {
        putBoolean(key, value)
    }

//    override fun registerListener(listener: AppPreferences.PreferenceListener) {
//        TODO("Not yet implemented")
//    }
//
//    override fun unregisterListener(listener: AppPreferences.PreferenceListener) {
//        TODO("Not yet implemented")
//    }

    private fun doubleToLong(value: Double) = java.lang.Double.doubleToRawLongBits(value)

    private fun longToDouble(value: Long) = java.lang.Double.longBitsToDouble(value)

}