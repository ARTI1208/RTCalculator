@file:JvmName("AndroidHelper")
package ru.art2000.helpers

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.StringRes
import androidx.annotation.ColorInt
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.annotation.DrawableRes
import java.util.*

fun Context.dip2px(dip: Float): Int = TypedValue
    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics)
    .toInt()

fun Context.getLocalizedResources(desiredLocale: Locale?): Resources {
    val currentConfiguration = resources.configuration
    val localizedConfiguration = Configuration(currentConfiguration)
    localizedConfiguration.setLocale(desiredLocale)
    val localizedContext = createConfigurationContext(localizedConfiguration)
    return localizedContext.resources
}

fun Context.getLocalizedString(
    desiredLocale: Locale?,
    @StringRes resId: Int
): String {
    return getLocalizedResources(desiredLocale).getString(resId)
}

@ColorInt
fun Context.getColorAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

@JvmOverloads
@DrawableRes
fun Context.getDrawableIdFromAttr(
    @AttrRes attribute: Int,
    @DrawableRes defaultId: Int = 0,
): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return if (typedValue.resourceId != 0) typedValue.resourceId else defaultId
}