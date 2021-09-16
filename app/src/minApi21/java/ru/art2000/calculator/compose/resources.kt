package ru.art2000.calculator.compose

import android.content.res.Resources
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.lang.IllegalArgumentException

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
@ReadOnlyComposable
fun textUnitResource(@DimenRes id: Int, type: TextUnitType): TextUnit {
    require(type != TextUnitType.Unspecified)

    val resources = resources()
    val density = LocalDensity.current
    val pxValue = resources.getDimension(id) / density.density

    return when (type) {
        TextUnitType.Sp -> pxValue.sp
        TextUnitType.Em -> pxValue.em
        else -> throw IllegalArgumentException("Invalid type")
    }
}
