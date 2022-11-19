package ru.art2000.calculator.calculator.vm

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal interface HistoryViewModel {

    val historyListItems: Flow<List<HistoryListItem>>

    fun copyHistoryItemToClipboard(item: HistoryValueItem, type: CopyMode): String

    fun updateHistoryItem(item: HistoryValueItem)

    fun removeHistoryItem(item: HistoryValueItem)

    fun clearHistoryDatabase()

    fun ensureDisplayResult(historyValueItem: HistoryValueItem): String

    companion object {
        private const val COPY_CATEGORY_BEGINNING = 100
        const val DELETE = 200
        const val COMMENT = 300
    }

    enum class CopyMode {
        ALL,
        EXPRESSION,
        RESULT;

        val id get() = COPY_CATEGORY_BEGINNING + ordinal

        companion object {

            private val cached = values()

            fun fromId(id: Int) = cached.getOrNull(id - COPY_CATEGORY_BEGINNING)
        }
    }
}