@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ru.art2000.extensions.preferences

import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class ReadOnlyPreferenceDelegate<V>(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: V,
    private val getter: SharedPreferences.() -> V,
) : ReadOnlyProperty<Any, V> {

    private var onUpdate: ((V) -> Unit)? = null

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key != this.key) return@OnSharedPreferenceChangeListener
        onUpdate?.invoke(sharedPreferences.getter())
    }

    private val observer = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): V {
        return preferences.getter()
    }

    fun <T> getAs(mapper: (V) -> T): ReadOnlyPreferenceDelegate<T> = ReadOnlyPreferenceDelegate(
        preferences, key, mapper(defaultValue)
    ) { mapper(getter()) }

    open fun listen(lifecycleOwner: LifecycleOwner, onUpdate: (V) -> Unit): ReadOnlyPreferenceDelegate<V> {

        this.onUpdate = onUpdate

        lifecycleOwner.lifecycle.removeObserver(observer)
        lifecycleOwner.lifecycle.addObserver(observer)

        return this
    }

    open fun listen(onUpdate: (V) -> Unit): ReadOnlyPreferenceDelegate<V> {
        return listen(ProcessLifecycleOwner.get(), onUpdate)
    }

    fun stopListening(lifecycleOwner: LifecycleOwner) {
        onUpdate = null
        lifecycleOwner.lifecycle.removeObserver(observer)
    }

    fun stopListening() {
        stopListening(ProcessLifecycleOwner.get())
    }

}

data class PreferenceDelegate<V>(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: V,
    private val getter: SharedPreferences.() -> V,
    private val setter: SharedPreferences.Editor.(V) -> Unit,
) : ReadOnlyPreferenceDelegate<V>(preferences, key, defaultValue, getter), ReadWriteProperty<Any, V> {

    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
        preferences.edit().apply { setter(value) }.apply()
    }

    fun mapGetter(mapper: (V) -> V): PreferenceDelegate<V> = PreferenceDelegate(
        preferences, key, defaultValue, { mapper(getter()) }, setter
    )

    fun <STORE> mapStore(mapping: StoreMapping<V, STORE>): PreferenceDelegate<V> {
        return PreferenceDelegate(preferences, key, defaultValue, { mapping.getter(this, key, defaultValue) }, { mapping.setter(this, key, it) })
    }

    fun <OPERATE> mapOperate(mapping: OperateMapping<OPERATE, V>): PreferenceDelegate<OPERATE> {
        return PreferenceDelegate(preferences, key, mapping.toOperate(defaultValue),
            { mapping.toOperate(getter()) }, { setter(mapping.toStore(it)) })
    }

    override fun listen(
        lifecycleOwner: LifecycleOwner,
        onUpdate: (V) -> Unit
    ) = super.listen(lifecycleOwner, onUpdate) as PreferenceDelegate<V>

    override fun listen(onUpdate: (V) -> Unit) = super.listen(onUpdate) as PreferenceDelegate<V>

    fun guard(guard: () -> Boolean): PreferenceDelegate<V> {
        return copy(setter = {
            if (guard()) {
                setter(it)
            }
        })
    }
}

interface OperateMapping<OPERATE, STORE> {

    fun toOperate(value: STORE): OPERATE

    fun toStore(value: OPERATE): STORE

}

interface StoreMapping<OPERATE, STORE> {

    fun getter(preferences: SharedPreferences, key: String, defaultValue: OPERATE): OPERATE

    fun setter(editor: SharedPreferences.Editor, key: String, value: OPERATE)

}

sealed class StringMapping<OPERATE> : StoreMapping<OPERATE, String> {

    protected abstract fun String.toOperate(): OPERATE

    override fun getter(preferences: SharedPreferences, key: String, defaultValue: OPERATE): OPERATE {
        val stringValue = preferences.getString(key, null) ?: return defaultValue
        return stringValue.toOperate()
    }

    override fun setter(editor: SharedPreferences.Editor, key: String, value: OPERATE) {
        editor.putString(key, value.toString())
    }
}

object IntStringMapping : StringMapping<Int>() {

    override fun String.toOperate() = toInt()

}

fun SharedPreferences.stringPreference(key: String, defaultValue: String) =
    PreferenceDelegate(this, key, defaultValue, { getString(key, defaultValue)!! }, { putString(key, it) })

fun SharedPreferences.nullableStringPreference(key: String, defaultValue: String? = null) =
    PreferenceDelegate(this, key, defaultValue, { getString(key, defaultValue) }, { putString(key, it) })

fun SharedPreferences.intPreference(key: String, defaultValue: Int) =
    PreferenceDelegate(this, key, defaultValue, { getInt(key, defaultValue) }, { putInt(key, it) })

fun SharedPreferences.longPreference(key: String, defaultValue: Long) =
    PreferenceDelegate(this, key, defaultValue, { getLong(key, defaultValue) }, { putLong(key, it) })

fun SharedPreferences.booleanPreference(key: String, defaultValue: Boolean, onUpdate: SharedPreferences.Editor.(Boolean) -> Unit = {}) =
    PreferenceDelegate(this, key, defaultValue, { getBoolean(key, defaultValue) }, {
        onUpdate(it)
        putBoolean(key, it)
    })

fun SharedPreferences.doublePreference(key: String, defaultValue: Double) = run {
    val defaultLong = doubleToLong(defaultValue)
    PreferenceDelegate(
        this,
        key,
        defaultValue,
        { longToDouble(getLong(key, defaultLong)) },
        { putLong(key, doubleToLong(it)) },
    )
}

private fun doubleToLong(value: Double) = java.lang.Double.doubleToRawLongBits(value)

private fun longToDouble(value: Long) = java.lang.Double.longBitsToDouble(value)