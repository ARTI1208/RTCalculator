package ru.art2000.calculator.model.calculator.history

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomHistoryRepository @Inject constructor(
    private val historyDao: CalculatorHistoryDao,
) : HistoryRepository {

    override fun getAll(): Flow<List<HistoryDatabaseItem>> {
        return historyDao.getAll()
    }

    override suspend fun add(item: HistoryDatabaseItem) {
        historyDao.insert(item)
    }

    override suspend fun remove(item: HistoryDatabaseItem) {
        historyDao.deleteById(item.id)
    }

    override suspend fun clear() {
        historyDao.clear()
    }
}