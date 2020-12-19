package ru.art2000.calculator.model.calculator

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.art2000.calculator.model.common.DiffComparable

@Entity(tableName = "history")
class HistoryItem(val expression: String, val result: String) : DiffComparable<HistoryItem> {

    val fullExpression: String get() = "$expression=$result"

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    override fun isSameItem(anotherItem: HistoryItem): Boolean {
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryItem): Boolean {
        return expression == anotherItem.expression && result == anotherItem.result
    }
}