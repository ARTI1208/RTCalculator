package ru.art2000.calculator.calculator.model

internal data class HistoryContentItem(
    val expression: String,
    val result: String,
    var comment: String?,
)
