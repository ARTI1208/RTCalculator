package ru.art2000.calculator.compose

import android.content.Context
import androidx.annotation.AttrRes
import androidx.compose.ui.graphics.Color
import ru.art2000.helpers.getColorAttribute

fun Context.getColorFromAttribute(@AttrRes colorAttr: Int) = Color(getColorAttribute(colorAttr))