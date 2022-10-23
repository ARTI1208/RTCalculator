package ru.art2000.extensions.views

import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat

var Window.isDrawingUnderStatusBarAllowed: Boolean
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = !decorView.fitsSystemWindows
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    set(canDraw) = WindowCompat.setDecorFitsSystemWindows(this, !canDraw)