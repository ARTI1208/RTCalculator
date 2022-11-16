package ru.art2000.extensions.preferences

interface AppPreferences {

    fun getString(key: String, defaultValue: String): String

    fun getNullableString(key: String, defaultValue: String?): String?

    fun getInt(key: String, defaultValue: Int): Int

    fun getLong(key: String, defaultValue: Long): Long

    fun getDouble(key: String, defaultValue: Double): Double

    fun getBoolean(key: String, defaultValue: Boolean): Boolean


    fun putString(key: String, value: String) = putNullableString(key, value)

    fun putNullableString(key: String, value: String?)

    fun putInt(key: String, value: Int)

    fun putLong(key: String, value: Long)

    fun putDouble(key: String, value: Double)

    fun putBoolean(key: String, value: Boolean)


    fun interface Listener<in V> {
        fun onChanged(value: V)

        fun onChanged(key: String, value: V) = onChanged(value)
    }

    // TODO
//    fun interface PreferenceListener {
//
//        fun onChanged(preferences: AppPreferences, key: String)
//
//    }
//
//    fun registerListener(listener: PreferenceListener)
//
//    fun unregisterListener(listener: PreferenceListener)

}

fun AppPreferences.stringPreference(key: String, defaultValue: String) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getString(key, defaultValue) },
        { putString(key, it) },
    )

fun AppPreferences.nullableStringPreference(key: String, defaultValue: String? = null) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getNullableString(key, defaultValue) },
        { putNullableString(key, it) },
    )

fun AppPreferences.intPreference(key: String, defaultValue: Int) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getInt(key, defaultValue) },
        { putInt(key, it) },
    )

fun AppPreferences.longPreference(key: String, defaultValue: Long) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getLong(key, defaultValue) },
        { putLong(key, it) },
    )

fun AppPreferences.booleanPreference(key: String, defaultValue: Boolean) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getBoolean(key, defaultValue) },
        { putBoolean(key, it) },
    )

fun AppPreferences.doublePreference(key: String, defaultValue: Double) =
    MutablePreferenceDelegate(
        this, key, defaultValue,
        { getDouble(key, defaultValue) },
        { putDouble(key, it) },
    )

inline fun <reified E : Enum<E>> AppPreferences.enumPreference(
    key: String,
    defaultValue: E,
) = enumPreference(key, defaultValue, object : StringMapping<E>() {

    override fun String.toOperate() = enumValueOf<E>(this)

    override fun E.toStore() = name
})

inline fun <reified E : Enum<E>> AppPreferences.enumPreference(
    key: String,
    defaultValue: E,
    storeMapping: StringMapping<E>,
): MutablePreferenceDelegate<E> {

    return stringPreference(key, with(storeMapping) { defaultValue.toStore() })
        .mapOperate(storeMapping)
}