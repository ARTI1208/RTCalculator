package ru.art2000.calculator.model.currency

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyItem::class], version = 2)
abstract class CurrencyRoomDB : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
}