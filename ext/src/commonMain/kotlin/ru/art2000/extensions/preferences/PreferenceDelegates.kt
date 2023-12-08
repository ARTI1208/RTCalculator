package ru.art2000.extensions.preferences

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class PreferenceDelegate<V> internal constructor(
    override val preferences: AppPreferences,
    override val key: String,
    protected val defaultValue: V,
    protected val getter: AppPreferences.() -> V,
) : IPreferenceDelegate<V> {

    @Suppress("LeakingThis")
    override val listenerDelegate = ListenerDelegate(this)

    override fun getValue() = preferences.getter()

    override fun <T> mapGetter(mapper: (V) -> T) = PreferenceDelegate(
        preferences, key, mapper(defaultValue)
    ) { mapper(getter()) }

}

class MutablePreferenceDelegate<V>(
    preferences: AppPreferences,
    key: String,
    defaultValue: V,
    getter: AppPreferences.() -> V,
    private val setter: AppPreferences.(V) -> Unit,
) : PreferenceDelegate<V>(preferences, key, defaultValue, getter), IMutablePreferenceDelegate<V> {

    override fun setValue(value: V) = preferences.setter(value)

    override fun mapGetter(mapper: (V) -> V) = MutablePreferenceDelegate(
        preferences, key, defaultValue, { mapper(getter()) }, setter
    )

    override fun <STORE> mapStore(mapping: StoreMapping<V, STORE>) = with(mapping) {
        MutablePreferenceDelegate(preferences, key, defaultValue,
            { get(key, defaultValue.toStore()).toOperate() },
            { set(key, it.toStore()) })
    }

    override fun <OPERATE> mapOperate(mapping: PreferenceMapping<OPERATE, V>) = with(mapping) {
        MutablePreferenceDelegate(preferences, key, defaultValue.toOperate(),
            { getter().toOperate() }, { setter(it.toStore()) })
    }

    override fun readonly(): PreferenceDelegate<V> = this

}

interface PreferenceMapping<OPERATE, STORE> {

    fun STORE.toOperate(): OPERATE

    fun OPERATE.toStore(): STORE

}

interface StoreMapping<OPERATE, STORE> : PreferenceMapping<OPERATE, STORE> {

    fun AppPreferences.get(key: String, defaultValue: STORE): STORE

    fun AppPreferences.set(key: String, value: STORE)

}

abstract class StringMapping<OPERATE> : StoreMapping<OPERATE, String> {

    override fun OPERATE.toStore() = toString()

    override fun AppPreferences.get(key: String, defaultValue: String): String {
        return getString(key, defaultValue)
    }

    override fun AppPreferences.set(key: String, value: String) {
        putString(key, value)
    }
}

object IntStringMapping : StringMapping<Int>() {

    override fun String.toOperate() = toInt()

}

typealias Subscription = () -> Unit

interface IPreferenceDelegate<V> : ReadOnlyProperty<Any, V> {

    val preferences: AppPreferences

    val key: String

    val listenerDelegate: ListenerDelegate<V>

    fun getValue(): V

    override fun getValue(thisRef: Any, property: KProperty<*>) = getValue()

    fun <T> mapGetter(mapper: (V) -> T): IPreferenceDelegate<T>

}

interface IMutablePreferenceDelegate<V>: IPreferenceDelegate<V>, ReadWriteProperty<Any, V> {

    override fun getValue(thisRef: Any, property: KProperty<*>) = getValue()

    fun setValue(value: V)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) = setValue(value)

    fun mapGetter(mapper: (V) -> V): IMutablePreferenceDelegate<V>

    fun <STORE> mapStore(mapping: StoreMapping<V, STORE>): IMutablePreferenceDelegate<V>

    fun <OPERATE> mapOperate(mapping: PreferenceMapping<OPERATE, V>): IMutablePreferenceDelegate<OPERATE>

    fun readonly(): IPreferenceDelegate<V> = this
}

fun <V, T : IPreferenceDelegate<V>> T.listen(listener: AppPreferences.Listener<V>): T {
    listenerDelegate.listen(listener)
    return this
}

fun <V, T : IPreferenceDelegate<V>> T.listen(callback: (V) -> Unit) = listen(listener = callback)

fun <V, T : IPreferenceDelegate<V>> T.observe(listener: AppPreferences.Listener<V>): Subscription {
    listenerDelegate.listen(listener)

    return { listenerDelegate.stopListening(listener) }
}

fun <V, T : IPreferenceDelegate<V>> T.observe(callback: (V) -> Unit): Subscription {

    val listener = AppPreferences.Listener<V> { callback(it) }

    listenerDelegate.listen(listener)

    return { listenerDelegate.stopListening(listener) }
}

