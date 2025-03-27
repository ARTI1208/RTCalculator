package ru.art2000.extensions.views

import android.view.Window
import androidx.core.view.WindowCompat

var Window.isDrawingUnderSystemBarsAllowed: Boolean
    get() = !decorView.fitsSystemWindows
    set(canDraw) = WindowCompat.setDecorFitsSystemWindows(this, !canDraw)