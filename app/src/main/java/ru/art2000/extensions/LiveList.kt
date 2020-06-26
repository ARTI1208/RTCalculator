package ru.art2000.extensions

import androidx.annotation.CallSuper

interface LiveList<E> : ExtendedMutableList<E>, LiveObservable<LiveList.LiveListObserver<E>> {

    abstract class LiveListObserver<E> {

        open fun onAnyChanged(previousList: List<E>) {}


        @CallSuper
        open fun onItemsReplaced(previousList: List<E>, replacedItems: Map<Int, E>) {
            onAnyChanged(previousList)
        }

        @CallSuper
        open fun onItemsInserted(previousList: List<E>, insertedItems: List<E>, position: Int) {
            onAnyChanged(previousList)
        }

        @CallSuper
        open fun onItemsRemoved(previousList: List<E>, removedItems: List<Int>) {
            onAnyChanged(previousList)
        }
    }
}