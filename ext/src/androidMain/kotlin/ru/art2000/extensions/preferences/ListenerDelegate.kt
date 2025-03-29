package ru.art2000.extensions.preferences

import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

actual class ListenerDelegate<V> actual constructor(
    private val property: IPreferenceDelegate<V>,
) {

    private val onUpdates = mutableMapOf<LifecycleOwner, MutableSet<AppPreferences.Listener<V>>>()

    private val observer = object : DefaultLifecycleObserver {

        private fun createListener(owner: LifecycleOwner) = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key != property.key) return@OnSharedPreferenceChangeListener

            val listeners = onUpdates[owner] ?: return@OnSharedPreferenceChangeListener

            val value = property.getValue()
            listeners.forEach { it.onChanged(property.key, value) }
        }

        private val listeners = mutableMapOf<LifecycleOwner, SharedPreferences.OnSharedPreferenceChangeListener>()

        override fun onCreate(owner: LifecycleOwner) {
            val listener = createListener(owner)
            listeners[owner] = listener
            (property.preferences as AndroidAppPreferences)
                .sharedPreferences
                .registerOnSharedPreferenceChangeListener(listener)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onUpdates.remove(owner)

            val listener = listeners.remove(owner) ?: return
            (property.preferences as AndroidAppPreferences)
                .sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun listen(lifecycleOwner: LifecycleOwner, listener: AppPreferences.Listener<V>) {
        onUpdates.getOrPut(lifecycleOwner) { mutableSetOf() } += listener

        lifecycleOwner.lifecycle.removeObserver(observer)
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    actual fun listen(listener: AppPreferences.Listener<V>) {
        listen(ProcessLifecycleOwner.get(), listener)
    }

    fun stopListening(lifecycleOwner: LifecycleOwner) {
        onUpdates.remove(lifecycleOwner)
        lifecycleOwner.lifecycle.removeObserver(observer)
    }

    @Suppress("unused")
    actual fun stopListening() {
        stopListening(ProcessLifecycleOwner.get())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun stopListening(lifecycleOwner: LifecycleOwner, listener: AppPreferences.Listener<V>) {
        onUpdates[lifecycleOwner]?.remove(listener)
    }

    actual fun stopListening(listener: AppPreferences.Listener<V>) {
        stopListening(ProcessLifecycleOwner.get(), listener)
    }

}

fun <V, D: PreferenceDelegate<V>> D.listen(lifecycleOwner: LifecycleOwner, listener: AppPreferences.Listener<V>): D {
    listenerDelegate.listen(lifecycleOwner, listener)
    return this
}

@Suppress("unused")
fun <V, D: PreferenceDelegate<V>> D.stopListening(lifecycleOwner: LifecycleOwner) {
    listenerDelegate.stopListening(lifecycleOwner)
}