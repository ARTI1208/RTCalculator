package ru.art2000.extensions.collections

import androidx.annotation.CallSuper

interface LiveList<E> : ExtendedMutableList<E>, LiveObservable<LiveList.LiveListObserver<E>> {

    /**
     * Contract: each of observer methods would be called 0..1 times (not counting overloads),
     * including `onAnyChanged`.
     *
     * Possible scenarios:
     * - onItemsInserted, onAnyChanged (onAnyChanged is always called last)
     * - onItemsInserted, onItemsRemoved, onAnyChanged (more than 2 methods could be called)
     */
    abstract class LiveListObserver<E> {

        /**
         * @param previousList - snapshot of list before it was changed
         * @param liveList - LiveList instance that reported changes
         */
        open fun onAnyChanged(previousList: List<E>, liveList: LiveList<E>) {
            onAnyChanged(previousList)
        }

        /**
         * @param previousList - snapshot of list before it was changed
         */
        open fun onAnyChanged(previousList: List<E>) {}

        /**
         * @param previousList - snapshot of list before it was changed
         * @param liveList - LiveList instance that reported changes
         * @param replacedIndices - indices of items in previousList that were replaced
         */
        @CallSuper
        open fun onItemsReplaced(previousList: List<E>, liveList: LiveList<E>, replacedIndices: List<Int>) {
            onItemsReplaced(previousList, replacedIndices)
        }

        /**
         * @param previousList - snapshot of list before it was changed
         * @param replacedIndices - indices of items in previousList that were replaced
         */
        @CallSuper
        open fun onItemsReplaced(previousList: List<E>, replacedIndices: List<Int>) {

        }

        /**
         * @param previousList - snapshot of list before it was changed
         * @param liveList - LiveList instance that reported changes
         * @param insertedIndices - indices of items  in previousList that were inserted
         */
        @CallSuper
        open fun onItemsInserted(previousList: List<E>, liveList: LiveList<E>, insertedIndices: List<Int>) {
            onItemsInserted(previousList, insertedIndices)
        }

        /**
         * @param previousList - snapshot of list before it was changed
         * @param insertedIndices - indices of items  in previousList that were inserted
         */
        @CallSuper
        open fun onItemsInserted(previousList: List<E>, insertedIndices: List<Int>) {

        }

        /**
         * @param previousList - snapshot of list before it was changed
         * @param liveList - LiveList instance that reported changes
         * @param removedIndices - indices of items in previousList that were removed
         */
        @CallSuper
        open fun onItemsRemoved(previousList: List<E>, liveList: LiveList<E>, removedIndices: List<Int>) {
            onItemsRemoved(previousList, removedIndices)
        }

        /**
         * @param previousList - snapshot of list before it was changed
         * @param removedIndices - indices of items in previousList that were removed
         */
        @CallSuper
        open fun onItemsRemoved(previousList: List<E>, removedIndices: List<Int>) {

        }
    }

    fun snapshot() = buildList { addAll(this@LiveList) }
}