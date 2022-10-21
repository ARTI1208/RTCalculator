package ru.art2000.calculator.model.calculator.history

import androidx.lifecycle.LiveData

interface HistoryRepository {

    fun getAll(): LiveData<List<HistoryDatabaseItem>>

    suspend fun add(item: HistoryDatabaseItem)

    suspend fun remove(item: HistoryDatabaseItem)

    suspend fun clear()

}