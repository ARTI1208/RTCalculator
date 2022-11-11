package ru.art2000.extensions.kt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

inline fun writeAndUpdateUi(
    crossinline compute: suspend () -> Unit,
    crossinline update: () -> Unit
) {
    CoroutineScope(Dispatchers.Default).launch {
        compute()
        CoroutineScope(Dispatchers.Main).launch {
            update()
        }
    }
}

// TODO reimplement when kotlin multiple receivers become available
fun <T> CoroutineScope.launchAndCollect(flow: Flow<T>, collector: FlowCollector<T>) {
    launch { flow.collect(collector) }
}