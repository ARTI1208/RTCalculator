package ru.art2000.extensions

class PermanentObserverWrapper<O>(override val observer: O): LiveObserverWrapper<O>(observer) {

    private var callback: (() -> Unit)? = null

    override fun subscribe(observerRemoveCallback: () -> Unit) {
        check(callback == null) { "Cannot subscribe same observer wrapper multiple times" }

        callback = observerRemoveCallback
    }

    override fun unsubscribe() {
        callback?.invoke()
    }
}