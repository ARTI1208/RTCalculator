package ru.art2000.calculator.currency.db.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import ru.art2000.calculator.currency.model.CurrencyRate

internal data class CurrencyDbRate(
    @PrimaryKey
    @ColumnInfo(name = "codeLetter")
    val code: String,
    val rate: Double,
)

internal fun CurrencyRate.toDbModel() = CurrencyDbRate(code, rate)
