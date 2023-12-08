package ru.art2000.extensions.preferences

import platform.Foundation.*

fun NSUserDefaults.stringPreference(key: String, defaultValue: String) =
    toAppPreferences().stringPreference(key, defaultValue)

fun NSUserDefaults.nullableStringPreference(key: String, defaultValue: String? = null) =
    toAppPreferences().nullableStringPreference(key, defaultValue)

fun NSUserDefaults.intPreference(key: String, defaultValue: Int) =
    toAppPreferences().intPreference(key, defaultValue)

fun NSUserDefaults.longPreference(key: String, defaultValue: Long) =
    toAppPreferences().longPreference(key, defaultValue)

fun NSUserDefaults.booleanPreference(key: String, defaultValue: Boolean) =
    toAppPreferences().booleanPreference(key, defaultValue)

fun NSUserDefaults.doublePreference(key: String, defaultValue: Double) =
    toAppPreferences().doublePreference(key, defaultValue)

fun NSUserDefaults.toAppPreferences() = IosAppPreferences(this)

val NSUserDefaults.Companion.standardAppPreferences
    get() = standardUserDefaults.toAppPreferences()

class IosAppPreferences internal constructor(val defaults: NSUserDefaults) : AppPreferences {

    override fun getString(key: String, defaultValue: String) = defaults.getOrDefault(key, defaultValue)

    override fun getNullableString(key: String, defaultValue: String?) = defaults.getOrDefault(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int) = defaults.getOrDefault(key, defaultValue.toLong()).toInt()

    override fun getLong(key: String, defaultValue: Long) = defaults.getOrDefault(key, defaultValue)

    override fun getDouble(key: String, defaultValue: Double) = defaults.getOrDefault(key, defaultValue)

    override fun getBoolean(key: String, defaultValue: Boolean) = defaults.getOrDefault(key, defaultValue)

    private inline fun <reified T> NSUserDefaults.getOrDefault(forKey: String, defaultValue: T): T {
        return objectForKey(forKey) as? T ?: defaultValue
    }

    override fun putNullableString(key: String, value: String?) {
        defaults.setObject(value, key)
    }

    override fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), key)
    }

    override fun putLong(key: String, value: Long) {
        defaults.setInteger(value, key)
    }

    override fun putDouble(key: String, value: Double) {
        defaults.setDouble(value, key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }

//    private val listeners = mutableSetOf<AppPreferences.PreferenceListener>()
//
//    override fun registerListener(listener: AppPreferences.PreferenceListener) {
//        listeners += listener
//        TODO()
//    }
//
//    override fun unregisterListener(listener: AppPreferences.PreferenceListener) {
//        listeners -= listener
//    }
}
