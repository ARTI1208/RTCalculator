package ru.art2000.extensions.layout

import android.view.View
import androidx.core.text.TextUtilsCompat
import java.util.Locale

val isLtr: Boolean
    get() = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR