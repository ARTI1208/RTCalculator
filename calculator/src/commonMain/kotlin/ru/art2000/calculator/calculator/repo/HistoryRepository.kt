package ru.art2000.calculator.calculator.repo

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal interface HistoryRepository {

    fun getAll(): Flow<List<HistoryListItem>>

    suspend fun add(item: HistoryContentItem)

    suspend fun update(item: HistoryValueItem)

    suspend fun remove(item: HistoryValueItem)

    suspend fun clear()

}