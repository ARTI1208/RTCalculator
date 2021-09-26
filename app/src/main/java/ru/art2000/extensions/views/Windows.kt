package ru.art2000.extensions.views

import android.os.Build
import android.view.Window
import androidx.core.view.WindowCompat

fun Window.allowDrawingUnderStatusBar(canDraw: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        WindowCompat.setDecorFitsSystemWindows(this, !canDraw)
    }
}