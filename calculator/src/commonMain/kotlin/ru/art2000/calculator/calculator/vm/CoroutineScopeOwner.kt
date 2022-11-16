package ru.art2000.calculator.calculator.vm

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeOwner {

    val coroutineScope: CoroutineScope

}