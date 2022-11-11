package ru.art2000.calculator.common

import android.content.Context

fun interface AppStartupListener {

    fun onAppStarted(context: Context)

}