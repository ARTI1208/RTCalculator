package ru.art2000.extensions.language

// Faster than String.toDoubleOrNull()
fun CharSequence.safeToDouble(): Double? = try {
    replace(spaceRegex, "").toDouble()
} catch (_: NumberFormatException) {
    null
}

val spaceRegex = Regex(" ")

fun String.dotSafeToDouble(): Double? = replace(',', '.').safeToDouble()

inline val Char.isOpeningBracket: Boolean
    get() = this == '('

inline val Char.isClosingBracket: Boolean
    get() = this == ')'

inline val Char.isSpacing: Boolean
    get() = this == ' '

inline val Char.isNumberSign: Boolean
    get() = this == '-' || this == '+'

inline val Char.isFloatingPointSymbol: Boolean
    get() = this == '.' || this == ','

inline val Char.isScientific: Boolean
    get() = this == 'e' || this == 'E'

fun CharArray.substring(from: Int, to: Int): String {
    return String(this, from, to - from)
}

inline val CharArray.length get() = size