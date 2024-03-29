package ru.art2000.extensions.arch

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun LifecycleOwner.launchRepeatOnStarted(crossinline foo: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            foo()
        }
    }
}

inline fun Fragment.launchRepeatOnStarted(crossinline foo: suspend CoroutineScope.() -> Unit) =
    viewLifecycleOwner.launchRepeatOnStarted(foo)