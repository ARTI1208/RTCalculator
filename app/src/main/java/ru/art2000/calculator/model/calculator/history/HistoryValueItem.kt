package ru.art2000.calculator.model.calculator.history

class HistoryValueItem(
        val dbItem: HistoryDatabaseItem,
) : HistoryListItem {

    override fun isSameItem(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return dbItem.isSameItem(anotherItem.dbItem)
    }

    override fun isContentSame(anotherItem: HistoryListItem): Boolean {
        if (anotherItem !is HistoryValueItem) return false
        return dbItem.isContentSame(anotherItem.dbItem)
    }
}