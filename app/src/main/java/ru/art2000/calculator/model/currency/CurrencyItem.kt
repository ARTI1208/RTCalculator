package ru.art2000.calculator.model.currency

import android.annotation.SuppressLint
import android.content.Context
import ru.art2000.calculator.model.common.DiffComparable
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity

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

private val codeToIdentifier = hashMapOf<String, Int>()

@SuppressLint("DiscouragedApi")
fun CurrencyItem.getNameIdentifier(context: Context) = codeToIdentifier.getOrPut(code) {
    context.resources.getIdentifier(
        "currency_$code",
        "string",
        context.packageName
    )
}