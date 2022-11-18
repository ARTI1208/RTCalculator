package ru.art2000.calculator.currency.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.art2000.calculator.currency.db.model.CurrencyDbItem

@Database(entities = [CurrencyDbItem::class], version = 3, exportSchema = false)
internal abstract class CurrencyRoomDB : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
}