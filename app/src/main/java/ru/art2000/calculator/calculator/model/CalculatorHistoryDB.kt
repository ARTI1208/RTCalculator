package ru.art2000.calculator.calculator.model

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.art2000.calculator.currency_converter.model.CurrencyDao

@Database(entities = [HistoryItem::class], version = 1)
abstract class CalculatorHistoryDB: RoomDatabase() {
    public abstract fun historyDao(): CalculatorHistoryDao
}