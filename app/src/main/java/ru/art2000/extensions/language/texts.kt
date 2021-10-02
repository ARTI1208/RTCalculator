package ru.art2000.extensions.language

// Faster than String.toDoubleOrNull()
fun String.safeToDouble(): Double? = try {
    replace(" ", "").toDouble()
} catch (_: NumberFormatException) {
    null
}

fun String.dotSafeToDouble(): Double? = replace(',', '.').safeToDouble()