package ru.art2000.calculator.currency.db.model

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.art2000.calculator.currency.model.CurrencyItem

@Entity(tableName = "currency")
internal data class CurrencyDbItem(
    @PrimaryKey
    @ColumnInfo(name = "codeLetter")
    val code: String,
    val rate: Double,
    var position: Int = -1,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyDbItem) return false
        return code == other.code
    }

    override fun hashCode() = code.hashCode()

}

internal fun CurrencyDbItem.toDomainModel() = CurrencyItem(code, rate, position)

internal fun CurrencyItem.toDbModel() = CurrencyDbItem(code, rate, position)