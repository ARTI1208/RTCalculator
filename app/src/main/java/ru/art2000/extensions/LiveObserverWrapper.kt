package ru.art2000.extensions

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

abstract class LiveObserverWrapper<O>(open val observer: O) {

    abstract fun subscribe(observerRemoveCallback: () -> Unit)

    abstract fun unsubscribe()
}