package ru.art2000.extensions.collections

import androidx.lifecycle.*

class LifecycleObserverWrapper<O>(
    val lifecycleOwner: LifecycleOwner,
    override val observer: O,
) : LiveObserverWrapper<O>(observer) {

    private var lifecycleObserver: LifecycleObserver? = null

    override fun subscribe(observerRemoveCallback: () -> Unit) {

        lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                observerRemoveCallback()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver!!)

    }

    override fun unsubscribe() {
        lifecycleObserver?.let { lifecycleOwner.lifecycle.removeObserver(it) }
    }

}