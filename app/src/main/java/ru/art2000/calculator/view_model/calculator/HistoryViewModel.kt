package ru.art2000.calculator.view_model.calculator

import androidx.lifecycle.LiveData
import ru.art2000.calculator.model.calculator.history.HistoryListItem
import ru.art2000.calculator.model.calculator.history.HistoryDatabaseItem

interface HistoryViewModel {

    val historyListItems: LiveData<List<HistoryListItem>>

    fun copyHistoryItemToClipboard(item: HistoryDatabaseItem, type: Int): String

    fun updateHistoryItem(item: HistoryDatabaseItem)

    fun removeHistoryItem(item: HistoryDatabaseItem)

    fun clearHistoryDatabase()

    companion object {
        const val COPY_ALL = 100
        const val COPY_EXPR = 101
        const val COPY_RES = 102
        const val DELETE = 200
        const val COMMENT = 300
    }
}