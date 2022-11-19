package ru.art2000.calculator.calculator.db.model

import platform.Foundation.NSDate
import platform.CoreData.NSFetchRequest
import platform.CoreData.NSManagedObject
import platform.CoreData.NSManagedObjectContext
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal data class HistoryDatabaseItem(
    val expression: String,
    val result: String,
    val date: NSDate,
    var comment: String?,
) {

    var id: Int = 0

    fun toValueItem() = HistoryValueItem(id, expression, result, comment)

    fun write(nsManagedObject: NSManagedObject) {
        nsManagedObject.apply {
            setValue(expression, "expression")
            setValue(result, "result")
            setValue(comment, "comment")
            setValue(date, "date")
        }
    }

    companion object {
        fun from(item: HistoryContentItem) = HistoryDatabaseItem(
            expression = item.expression,
            result = item.result,
            date = NSDate(),
            comment = item.comment,
        )

        fun read(data: NSManagedObject) = HistoryDatabaseItem(
            expression = data.valueForKey("expression") as String,
            result = data.valueForKey("result") as String,
            date = data.valueForKey("date") as NSDate,
            comment = data.valueForKey("comment") as String?,
        ).apply {
            id = data.objectID.hash.toInt()
        }
    }
}