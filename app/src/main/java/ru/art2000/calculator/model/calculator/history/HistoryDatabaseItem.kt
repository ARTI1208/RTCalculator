package ru.art2000.calculator.model.calculator.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.art2000.calculator.model.common.DiffComparable
import java.util.*

@Entity(tableName = "history")
class HistoryDatabaseItem(
        val expression: String,
        val result: String,
        val date: Calendar,
        var comment: String? = null,
) : DiffComparable<HistoryDatabaseItem> {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    val fullExpression: String get() = "$expression=$result"

    override fun isSameItem(anotherItem: HistoryDatabaseItem): Boolean {
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryDatabaseItem): Boolean {
        return expression == anotherItem.expression && result == anotherItem.result
    }
}