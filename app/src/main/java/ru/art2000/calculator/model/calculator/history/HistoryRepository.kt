package ru.art2000.calculator.model.calculator.history

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getAll(): Flow<List<HistoryDatabaseItem>>

    suspend fun add(item: HistoryDatabaseItem)

    suspend fun remove(item: HistoryDatabaseItem)

    suspend fun clear()

}