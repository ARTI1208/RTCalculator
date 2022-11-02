package ru.art2000.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.util.*

@Suppress("deprecation")
fun <T> Context.getLocalizedResource(desiredLocale: Locale, resourceGetter: Resources.() -> T): T {
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
)= getLocalizedResource(desiredLocale) { getString(resId) }

@ColorInt
fun Context.getColorAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return when (typedValue.type) {
        TypedValue.TYPE_ATTRIBUTE -> ContextCompat.getColor(this, typedValue.resourceId)
        in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT -> typedValue.data
        else -> throw Exception("Unsupported color attribute type: ${typedValue.type}")
    }
}

@Dimension
fun Context.getDimenAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return when (typedValue.type) {
        TypedValue.TYPE_ATTRIBUTE -> resources.getDimensionPixelOffset(typedValue.resourceId)
        TypedValue.TYPE_DIMENSION -> TypedValue.complexToDimensionPixelOffset(typedValue.data, resources.displayMetrics)
        else -> throw Exception("Unsupported dimen attribute type: ${typedValue.type}")
    }
}