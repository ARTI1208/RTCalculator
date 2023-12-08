package ru.art2000.extensions

import kotlinx.datetime.*

fun timeStringToSeconds(time: String): Int {
    val (first, second) = parseStringTime(time)
    return (first * 60 + second) * 60
}

fun parseStringTime(time: String): Pair<Int, Int> {
    val parts = time.split(':')
    val e = IllegalArgumentException("String '$time' is not time string")
    if (parts.size != 2) throw e

    val hour = (parts.first().toIntOrNull() ?: throw e)
        .coerceAtLeast(0)
        .coerceAtMost(23)

    val minute = (parts.last().toIntOrNull() ?: throw e)
        .coerceAtLeast(0)
        .coerceAtMost(59)

    return hour to minute
}

val LocalDate.timeInMillis
    get() = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun LocalDate(epochMilliseconds: Long) =
    Instant.fromEpochMilliseconds(epochMilliseconds)
        .toLocalDateTime(TimeZone.UTC).date