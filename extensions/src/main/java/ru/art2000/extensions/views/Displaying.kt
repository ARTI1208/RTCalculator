package ru.art2000.extensions.views

import android.os.Build
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.*

private val fourDigitYearShortFormatter by lazy {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Locale.getDefault(Locale.Category.FORMAT)
    else
        Locale.getDefault()

    val pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        locale,
    ).let {
        if (it.contains("yyyy")) it
        else it.replace("yy", "yyyy")
    }

    DateTimeFormatter.ofPattern(pattern)
}

fun LocalDate.toViewString() = format(fourDigitYearShortFormatter)