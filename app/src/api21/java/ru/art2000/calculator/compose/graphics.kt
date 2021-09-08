package ru.art2000.calculator.compose

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.integerResource
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import ru.art2000.helpers.getColorAttribute
import ru.art2000.helpers.getDrawableIdFromAttr

fun Context.getColorFromAttribute(@AttrRes colorAttr: Int) =
    Color(getColorAttribute(colorAttr))

@Composable
fun Context.attributeDrawablePainter(
    @AttrRes attrRes: Int,
    @DrawableRes defaultId: Int = 0
): Painter {

//    integerResource(id = )

    val drawableId = getDrawableIdFromAttr(attrRes, defaultId)
    return rememberImagePainter(ContextCompat.getDrawable(this, drawableId))
}