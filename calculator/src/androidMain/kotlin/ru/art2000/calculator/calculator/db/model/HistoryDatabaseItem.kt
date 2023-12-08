package ru.art2000.calculator.calculator.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryValueItem
import ru.art2000.extensions.collections.DiffComparable
import java.util.*

@Entity(tableName = "history")
internal data class HistoryDatabaseItem(
    val expression: String,
    val angle: AngleType,
    val date: Calendar,
    var comment: String?,
    val result: String,
) : DiffComparable<HistoryDatabaseItem> {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun isSameItem(anotherItem: HistoryDatabaseItem): Boolean {
        return id == anotherItem.id
    }

    override fun isContentSame(anotherItem: HistoryDatabaseItem): Boolean {
        return expression == anotherItem.expression && angle == anotherItem.angle
    }

    fun toValueItem() = HistoryValueItem(id, expression, angle, comment, result)

    companion object {

        fun of(item: HistoryContentItem) = with(item) {
            HistoryDatabaseItem(expression, angleType, Calendar.getInstance(), comment, "")
        }
    }
}