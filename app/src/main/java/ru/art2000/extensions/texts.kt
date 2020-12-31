package ru.art2000.extensions

public fun String.safeToDouble(): Double? = try {
    toDouble()
} catch (_ : NumberFormatException) {
    null
}

public fun String.insert(any: Any, position: Int, positionEnd: Int = position): String {
    return substring(0, position) + any + substring(positionEnd)
}