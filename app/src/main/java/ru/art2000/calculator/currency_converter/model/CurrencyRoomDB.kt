package ru.art2000.calculator.currency_converter.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyItem::class, InfoItem::class], version = 2, exportSchema = true)
abstract class CurrencyRoomDB : RoomDatabase() {
    public abstract fun currencyDao(): CurrencyDao
}