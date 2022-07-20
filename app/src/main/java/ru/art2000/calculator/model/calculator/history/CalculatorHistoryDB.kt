package ru.art2000.calculator.model.calculator.history

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.art2000.calculator.model.common.RoomConverters

@Database(entities = [HistoryDatabaseItem::class], version = 2)
@TypeConverters(RoomConverters::class)
abstract class CalculatorHistoryDB : RoomDatabase() {

    abstract fun historyDao(): CalculatorHistoryDao
}