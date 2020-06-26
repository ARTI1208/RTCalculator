package ru.art2000.extensions

import androidx.annotation.CallSuper

interface LiveMap<K, V> : Map<K, V>, LiveObservable<LiveMap.LiveMapObserver<K, V>> {

    abstract class LiveMapObserver<K, V> {

        open fun onAnyChanged(previousMap: Map<K, V>) {}

        @CallSuper
        open fun onItemsPut(previousMap: Map<K, V>, newItems: List<Map.Entry<K, V>>) {
            onAnyChanged(previousMap)
        }

        @CallSuper
        open fun onItemsRemoved(previousMap: Map<K, V>, removedItems:  List<Map.Entry<K, V>>) {
            onAnyChanged(previousMap)
        }
    }

}