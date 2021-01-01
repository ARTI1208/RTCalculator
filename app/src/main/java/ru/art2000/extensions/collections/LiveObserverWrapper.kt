package ru.art2000.extensions.collections

abstract class LiveObserverWrapper<O>(open val observer: O) {

    abstract fun subscribe(observerRemoveCallback: () -> Unit)

    abstract fun unsubscribe()
}