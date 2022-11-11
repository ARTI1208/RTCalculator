package ru.art2000.calculator.calculator.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import ru.art2000.calculator.calculator.db.CalculatorHistoryDao
import ru.art2000.calculator.calculator.db.model.HistoryDatabaseItem
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryDateItem
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem
import java.util.*
import javax.inject.Inject

internal class RoomHistoryRepository @Inject constructor(
    private val historyDao: CalculatorHistoryDao,
) : HistoryRepository {

    override fun getAll(): Flow<List<HistoryListItem>> {
        return historyDao.getAll().map { items ->
            var calendar: Calendar? = null
            items.fold(mutableListOf()) { acc, historyDatabaseItem ->
                if (!isSameDay(calendar, historyDatabaseItem.date)) {
                    val date = historyDatabaseItem.date.let {
                        LocalDate(it[Calendar.YEAR], it[Calendar.MONTH] + 1,
                            it[Calendar.DAY_OF_MONTH])
                    }
                    acc += HistoryDateItem(date)
                    calendar = historyDatabaseItem.date
                }
                acc += historyDatabaseItem.toValueItem()
                acc
            }
        }
    }

    override suspend fun add(item: HistoryContentItem) {
        historyDao.insert(HistoryDatabaseItem.of(item))
    }

    override suspend fun update(item: HistoryValueItem) {
        historyDao.update(item)
    }

    override suspend fun remove(item: HistoryValueItem) {
        historyDao.deleteById(item.id)
    }

    override suspend fun clear() {
        historyDao.clear()
    }

    private fun isSameDay(calendar: Calendar?, otherCalendar: Calendar?): Boolean {
        if (calendar == null) return otherCalendar == null
        if (otherCalendar == null) return false

        return calendar[Calendar.DAY_OF_YEAR] == otherCalendar[Calendar.DAY_OF_YEAR]
                && calendar[Calendar.YEAR] == otherCalendar[Calendar.YEAR]
    }
}