@file:JvmName("AndroidHelper")

package ru.art2000.helpers

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.util.*

fun Context.dip2px(dip: Float): Int = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics)
        .toInt()

@Suppress("deprecation")
fun <T> Context.getLocalizedResource(desiredLocale: Locale, resourceGetter: (Resources) -> T): T {
    val currentConfiguration = resources.configuration
    val localizedConfiguration = Configuration(currentConfiguration)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        localizedConfiguration.setLocale(desiredLocale)
        val localizedContext = createConfigurationContext(localizedConfiguration)
        resourceGetter(localizedContext.resources)
    } else {
        localizedConfiguration.locale = desiredLocale
        val localizedResources = Resources(assets, resources.displayMetrics, localizedConfiguration)
        val resource = resourceGetter(localizedResources)
        resources.updateConfiguration(currentConfiguration, resources.displayMetrics)
        resource
    }
}

fun Context.getLocalizedString(
        desiredLocale: Locale,
        @StringRes resId: Int
)= getLocalizedResource(desiredLocale) { it.getString(resId) }

fun Context.getLocalizedArray(
        desiredLocale: Locale,
        @ArrayRes resId: Int
): Array<String> = getLocalizedResource(desiredLocale) { it.getStringArray(resId) }

@ColorInt
fun Context.getColorAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

@Dimension
fun Context.getDimenAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return resources.getDimensionPixelOffset(typedValue.resourceId)
}