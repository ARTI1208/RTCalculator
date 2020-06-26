package ru.art2000.extensions

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.jakewharton.rxrelay3.Relay
import io.reactivex.rxjava3.core.Observer
import java.util.function.Consumer


@FunctionalInterface
interface Consumer<T> {

    fun consume(value: T)

}

public fun <T> Relay<T>.subscribe(lifecycleOwner: LifecycleOwner, consumer: ru.art2000.extensions.Consumer<in T>) {
    val disposable = subscribe { consumer.consume(it) }

    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            Log.d("OnD", "yeah")
            disposable.dispose()
        }
    })

}

public fun <T> Relay<T>.subscribe(lifecycleOwner: LifecycleOwner, runnable: Runnable) {
    val disposable = subscribe { runnable.run() }

    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            Log.d("OnD", "yeah")
            disposable.dispose()
        }
    })

}