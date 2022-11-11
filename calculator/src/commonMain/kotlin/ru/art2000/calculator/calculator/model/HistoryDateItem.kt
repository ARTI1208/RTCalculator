package ru.art2000.calculator.calculator.model

import kotlinx.datetime.LocalDate

internal class HistoryDateItem(
    val date: LocalDate,
) : HistoryListItem {

    override fun isSameItem(anotherItem: HistoryListItem) = this === anotherItem

    override fun isContentSame(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryDateItem) return false
        return date == anotherItem.date
    }
}