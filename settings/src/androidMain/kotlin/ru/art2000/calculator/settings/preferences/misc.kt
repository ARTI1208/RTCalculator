package ru.art2000.calculator.settings.preferences

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
val DYNAMIC_COLORS_AVAILABLE get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S