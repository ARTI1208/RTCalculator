package ru.art2000.extensions.preferences

import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaultsDidChangeNotification
import platform.darwin.NSObjectProtocol

actual class ListenerDelegate<V> actual constructor(
    private val property: IPreferenceDelegate<V>,
) {

    private val onUpdates = mutableSetOf<AppPreferences.Listener<V>>()

    private fun createListener(): (NSNotification?) -> Unit {
        var previousValue = property.getValue()

        return block@ { _: NSNotification? ->

            val current = property.getValue()
            if (previousValue == current) return@block
            previousValue = current

            onUpdates.forEach { it.onChanged(property.key, current) }
        }
    }

    private var observer: NSObjectProtocol? = null

    private fun registerListenerIfNeeded() {
        if (observer != null) return
        observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = (property.preferences as IosAppPreferences).defaults,
            queue = null,
            usingBlock = createListener(),
        )
    }

    private fun unregisterListenerIfNeeded() {
        if (onUpdates.isNotEmpty()) return
        observer?.also {
            NSNotificationCenter.defaultCenter.removeObserver(it, NSUserDefaultsDidChangeNotification, null)
            observer = null
        }
    }

    actual fun listen(listener: AppPreferences.Listener<V>) {
        onUpdates += listener
        registerListenerIfNeeded()
    }

    actual fun stopListening() {
        onUpdates.clear()
        unregisterListenerIfNeeded()
    }

    actual fun stopListening(listener: AppPreferences.Listener<V>) {
        onUpdates -= listener
        unregisterListenerIfNeeded()
    }

}