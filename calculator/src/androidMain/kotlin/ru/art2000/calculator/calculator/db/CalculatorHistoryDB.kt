package ru.art2000.calculator.calculator.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.art2000.calculator.calculator.db.model.HistoryDatabaseItem
import ru.art2000.calculator.common.db.RoomConverters

@Database(entities = [HistoryDatabaseItem::class], version = 4, exportSchema = false)
@TypeConverters(RoomConverters::class)
internal abstract class CalculatorHistoryDB : RoomDatabase() {

    abstract fun historyDao(): CalculatorHistoryDao
}