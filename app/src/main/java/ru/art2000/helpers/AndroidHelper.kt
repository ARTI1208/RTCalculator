@file:JvmName("AndroidHelper")

package ru.art2000.helpers

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.*

fun Context.dip2px(dip: Float): Int = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics)
        .toInt()

fun Context.getLocalizedResources(desiredLocale: Locale?): Resources {
    val currentConfiguration = resources.configuration
    val localizedConfiguration = Configuration(currentConfiguration)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        localizedConfiguration.setLocale(desiredLocale)
        val localizedContext = createConfigurationContext(localizedConfiguration)
        localizedContext.resources
    } else {
        localizedConfiguration.locale = desiredLocale
        val localizedResources = Resources(assets, resources.displayMetrics, localizedConfiguration)
        resources.updateConfiguration(currentConfiguration, resources.displayMetrics)
        localizedResources
    }
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