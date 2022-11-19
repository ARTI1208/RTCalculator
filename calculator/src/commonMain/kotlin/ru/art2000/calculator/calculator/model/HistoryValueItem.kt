package ru.art2000.calculator.calculator.model

data class HistoryValueItem(
    val id: Int,
    val expression: String,
    val angle: AngleType,
    var comment: String?,
    val result: String = "",
) : HistoryListItem {

    override fun isSameItem(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return expression == anotherItem.expression
                && angle == anotherItem.angle
                && comment == anotherItem.comment
    }
}