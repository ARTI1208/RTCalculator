package ru.art2000.extensions

import androidx.annotation.CallSuper
import androidx.databinding.ObservableList

open class SimpleOnListChangedCallback<T : ObservableList<*>> : ObservableList.OnListChangedCallback<T>(){

    open fun onAnyChanged(sender: T) {

    }

    @CallSuper
    override fun onChanged(sender: T) {
        onAnyChanged(sender)
    }

    @CallSuper
    override fun onItemRangeRemoved(sender: T, positionStart: Int, itemCount: Int) {
        onAnyChanged(sender)
    }

    @CallSuper
    override fun onItemRangeMoved(sender: T, fromPosition: Int, toPosition: Int, itemCount: Int) {
        onAnyChanged(sender)
    }

    @CallSuper
    override fun onItemRangeInserted(sender: T, positionStart: Int, itemCount: Int) {
        onAnyChanged(sender)
    }

    @CallSuper
    override fun onItemRangeChanged(sender: T, positionStart: Int, itemCount: Int) {
        onAnyChanged(sender)
    }
}