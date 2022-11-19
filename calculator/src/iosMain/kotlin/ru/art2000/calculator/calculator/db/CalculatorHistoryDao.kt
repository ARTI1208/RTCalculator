package ru.art2000.calculator.calculator.db

import kotlinx.cinterop.objcPtr
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreData.*
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSPredicate
import ru.art2000.calculator.calculator.db.model.HistoryDatabaseItem
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal class CalculatorHistoryDao {

    private val container by lazy {
        NSPersistentContainer("HistoryModel").apply {
            loadPersistentStoresWithCompletionHandler { _, _ -> }
        }
    }

    private val idMap = mutableMapOf<Int, NSManagedObjectID>()

    private val context by lazy {
        container.newBackgroundContext()
    }

    fun getAll(): Flow<List<HistoryDatabaseItem>> = callbackFlow {
        val block = { _: NSNotification? ->
            trySendBlocking(readAll())
            Unit
        }

        val observers = listOf(
            NSManagedObjectContextDidSaveNotification,
        ).map {
            NSNotificationCenter.defaultCenter.addObserverForName(
                name = it,
                `object` = context,
                queue = null,
                usingBlock = block,
            ) to it
        }

        trySendBlocking(readAll())

        awaitClose {
            observers.forEach { (observer, name) ->
                NSNotificationCenter.defaultCenter.removeObserver(observer, name, null)
            }
        }
    }

    private fun readAll(): List<HistoryDatabaseItem> {
        idMap.clear()
        return context.executeFetchRequest(fetchRequest(), null)?.map {
            it as NSManagedObject
            HistoryDatabaseItem.read(it).apply {
                idMap[id] = it.objectID
            }
        } ?: emptyList()
    }

    suspend fun insert(item: HistoryContentItem): Long {

        val res = NSEntityDescription.insertNewObjectForEntityForName(
            entityName = "HistoryDatabaseItem",
            inManagedObjectContext = context,
        )

        HistoryDatabaseItem.from(item).write(res)

        context.save(null)

        return -1
    }

    suspend fun update(item: HistoryValueItem) {
        context.apply {
            forObjectsWithId(item.id) {
                it.setValue(item.expression, "expression")
                it.setValue(item.result, "result")
                it.setValue(item.comment, "comment")
            }
            save(null)
        }
    }

    suspend fun deleteById(id: Int): Int {
        context.apply {
            forObjectsWithId(id) {
                deleteObject(it)
            }
            save(null)
        }
        return -1
    }

    suspend fun clear(): Int {
        val batchRequest = NSBatchDeleteRequest(fetchRequest())
        context.executeRequest(batchRequest, null)
        context.save(null)

        return -1
    }

    private fun fetchRequest(): NSFetchRequest {
        return NSFetchRequest(entityName = "HistoryDatabaseItem")
    }

    private fun idFetchRequest(id: Int): NSFetchRequest {
        return fetchRequest().apply {
            predicate = NSPredicate.predicateWithFormat("self == %@", idMap[id])
        }
    }

    private fun NSManagedObjectContext.forObjectsWithId(id: Int, foo: (NSManagedObject) -> Unit) {
        executeFetchRequest(idFetchRequest(id), null)?.forEach {
            foo(it as NSManagedObject)
        }
    }
}