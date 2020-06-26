package ru.art2000.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class LifecycleObserverWrapper<O>(val lifecycleOwner: LifecycleOwner,
                                   override val observer: O): LiveObserverWrapper<O>(observer) {

    private var lifecycleObserver: LifecycleObserver? = null

    override fun subscribe(observerRemoveCallback: () -> Unit) {

        lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                observerRemoveCallback()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver!!)

    }

    override fun unsubscribe() {
        lifecycleObserver?.let { lifecycleOwner.lifecycle.removeObserver(it) }
    }

}