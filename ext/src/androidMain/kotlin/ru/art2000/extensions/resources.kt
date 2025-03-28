package ru.art2000.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.util.*

fun <T> Context.getLocalizedResource(desiredLocale: Locale, resourceGetter: Resources.() -> T): T {
    val currentConfiguration = resources.configuration
    val localizedConfiguration = Configuration(currentConfiguration)
    localizedConfiguration.setLocale(desiredLocale)
    val localizedContext = createConfigurationContext(localizedConfiguration)
    return resourceGetter(localizedContext.resources)
}

fun Context.getLocalizedString(
        desiredLocale: Locale,
        @StringRes resId: Int
)= getLocalizedResource(desiredLocale) { getString(resId) }

@ColorInt
fun Context.getColorFromAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return when (typedValue.type) {
        TypedValue.TYPE_ATTRIBUTE -> ContextCompat.getColor(this, typedValue.resourceId)
        in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT -> typedValue.data
        else -> throw Exception("Unsupported color attribute type '${typedValue.type}' for ${resources.getResourceName(typedValue.resourceId)}")
    }
}

@Dimension
fun Context.getDimenFromAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return when (typedValue.type) {
        TypedValue.TYPE_ATTRIBUTE -> resources.getDimensionPixelOffset(typedValue.resourceId)
        TypedValue.TYPE_DIMENSION -> TypedValue.complexToDimensionPixelOffset(typedValue.data, resources.displayMetrics)
        else -> throw Exception("Unsupported dimen attribute type: ${typedValue.type}")
    }
}

@Suppress("unused")
@DrawableRes
fun Context.getDrawableResFromAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return when (val id = typedValue.resourceId) {
        0 -> throw Exception("Unsupported dimen attribute type: ${typedValue.type}")
        else -> id
    }
}