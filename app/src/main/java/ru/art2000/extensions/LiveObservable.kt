package ru.art2000.extensions

import androidx.lifecycle.LifecycleOwner

interface LiveObservable<O> {

    fun observe(lifecycleOwner: LifecycleOwner, observer: O)

    fun observeForever(observer: O)

//    fun removeObserver(lifecycleOwner: LifecycleOwner, observer: O)

    fun removeObserver(observer: O)

    fun removeAllObservers(lifecycleOwner: LifecycleOwner)

}