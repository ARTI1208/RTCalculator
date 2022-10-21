package ru.art2000.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun writeAndUpdateUi(
    crossinline compute: suspend () -> Unit,
    crossinline update: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        compute()
        CoroutineScope(Dispatchers.Main).launch {
            update()
        }
    }
}