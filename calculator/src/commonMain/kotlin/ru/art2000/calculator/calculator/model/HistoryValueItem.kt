package ru.art2000.calculator.calculator.model

data class HistoryValueItem(
    val id: Int,
    val expression: String,
    val result: String,
    var comment: String?,
) : HistoryListItem {

    val fullExpression: String get() = "$expression=$result"

    override fun isSameItem(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return expression == anotherItem.expression
                && result == anotherItem.result
                && comment == anotherItem.comment
    }
}