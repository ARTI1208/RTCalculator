package ru.art2000.calculator.calculator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.art2000.calculator.model.DiffComparable

@Entity(tableName = "history")
class HistoryItem(val expression: String, val result: String) : DiffComparable<HistoryItem> {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    override fun isSameItem(anotherItem: HistoryItem): Boolean {
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryItem): Boolean {
        return expression == anotherItem.expression && result == anotherItem.result
    }

}