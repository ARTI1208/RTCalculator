package ru.art2000.calculator.calculator.model

internal data class HistoryContentItem(
    val expression: String,
    val angleType: AngleType,
    var comment: String?,
)
