package ru.art2000.calculator.model.currency

import android.annotation.SuppressLint
import android.content.Context
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
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyItem) return false
        return code == other.code
    }

    override fun hashCode() = code.hashCode()

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