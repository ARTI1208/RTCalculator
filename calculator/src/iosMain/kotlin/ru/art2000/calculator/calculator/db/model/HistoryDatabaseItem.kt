package ru.art2000.calculator.calculator.db.model

import platform.Foundation.NSDate
import platform.CoreData.NSManagedObject
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal data class HistoryDatabaseItem(
    val expression: String,
    val angle: AngleType,
    val date: NSDate,
    var comment: String?,
    val result: String = "",
) {

    var id: Int = 0

    fun toValueItem() = HistoryValueItem(id, expression, angle, comment, result)

    fun write(nsManagedObject: NSManagedObject) {
        nsManagedObject.apply {
            setValue(expression, "expression")
            setValue(result, "result")
            setValue(angle.name, "angle")
            setValue(comment, "comment")
            setValue(date, "date")
        }
    }

    companion object {
        fun from(item: HistoryContentItem) = HistoryDatabaseItem(
            expression = item.expression,
            angle = item.angleType,
            date = NSDate(),
            comment = item.comment,
        )

        fun read(data: NSManagedObject) = HistoryDatabaseItem(
            expression = data.valueForKey("expression") as String,
            result = data.valueForKey("result") as String,
            angle = (data.valueForKey("angle") as String?)?.let { enumValueOf<AngleType>(it) } ?: AngleType.DEGREES,
            date = data.valueForKey("date") as NSDate,
            comment = data.valueForKey("comment") as String?,
        ).apply {
            id = data.objectID.hash.toInt()
        }
    }
}