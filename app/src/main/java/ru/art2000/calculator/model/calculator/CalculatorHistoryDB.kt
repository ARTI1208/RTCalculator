package ru.art2000.calculator.model.calculator

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistoryItem::class], version = 1)
abstract class CalculatorHistoryDB: RoomDatabase() {
    public abstract fun historyDao(): CalculatorHistoryDao
}