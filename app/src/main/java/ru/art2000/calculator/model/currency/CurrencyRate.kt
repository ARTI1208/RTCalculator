package ru.art2000.calculator.model.currency

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class CurrencyRate(
    @PrimaryKey
    @ColumnInfo(name = "codeLetter")
    val code: String,
    val rate: Double,
)
