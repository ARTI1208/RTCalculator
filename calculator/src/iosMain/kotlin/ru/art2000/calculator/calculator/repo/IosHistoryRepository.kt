package ru.art2000.calculator.calculator.repo

import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import platform.Foundation.*
import ru.art2000.calculator.calculator.db.CalculatorHistoryDao
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryDateItem
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal class IosHistoryRepository : HistoryRepository {

    private val historyDao = CalculatorHistoryDao()

    override fun getAll() = historyDao.getAll().map { items ->
        val nsCalendar = NSCalendar.currentCalendar

        var previousDate: NSDate? = null
        items.fold(mutableListOf<HistoryListItem>()) { acc, historyDatabaseItem ->
            if (!nsCalendar.isSameDay(previousDate, historyDatabaseItem.date)) {

                val components = nsCalendar.components(
                    NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
                    historyDatabaseItem.date,
                )

                val date = components.let {
                    LocalDate(
                        it.year.toInt(), it.month.toInt(), it.day.toInt()
                    )
                }
                acc += HistoryDateItem(date)
                previousDate = historyDatabaseItem.date
            }
            acc += historyDatabaseItem.toValueItem()
            acc
        }
    }


    override suspend fun add(item: HistoryContentItem) {
        historyDao.insert(item)
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

    private fun NSCalendar.isSameDay(date: NSDate?, other: NSDate?): Boolean {
        if (date == null) return other == null
        if (other == null) return false

        return compareDate(date, other, NSCalendarUnitDay) == NSOrderedSame
    }
}