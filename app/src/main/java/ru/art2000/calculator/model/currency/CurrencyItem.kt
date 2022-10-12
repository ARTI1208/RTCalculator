package ru.art2000.calculator.model.currency

import ru.art2000.calculator.model.common.DiffComparable
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "currency")
data class CurrencyItem(
    @PrimaryKey
    @ColumnInfo(name = "codeLetter")
    val code: String,
    val rate: Double,
    var position: Int = -1,
) : DiffComparable<CurrencyItem> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyItem) return false
        return code == other.code
    }

    override fun hashCode() = code.hashCode()

    override fun isSameItem(anotherItem: CurrencyItem) = code == anotherItem.code

    override fun isContentSame(anotherItem: CurrencyItem) = position == anotherItem.position
}