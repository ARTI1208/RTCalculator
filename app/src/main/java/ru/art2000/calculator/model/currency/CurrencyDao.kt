package ru.art2000.calculator.model.currency

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    fun getAll(): List<CurrencyItem>

    @Query("SELECT * FROM currency WHERE position >= 0 ORDER BY position")
    fun getVisibleItems(): LiveData<List<CurrencyItem>>

    @Query("SELECT COUNT(*) FROM currency WHERE position >= 0")
    fun countVisibleItems(): Int

    @Query("SELECT codeLetter FROM currency WHERE position >= 0")
    fun getVisibleItemCodes(): List<String>

    @Query("SELECT * FROM currency WHERE position < 0 ORDER BY codeLetter")
    fun getHiddenItems(): LiveData<List<CurrencyItem>>

    @Query("SELECT * FROM currency WHERE codeLetter = :code ")
    fun getByCode(code: String): CurrencyItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: CurrencyItem): Long

    @Query("UPDATE currency SET rate = :rate WHERE codeLetter = :code")
    fun updateRate(code: String, rate: Double): Int

    @Query("UPDATE currency SET position = :position WHERE codeLetter = :code")
    fun updatePosition(code: String, position: Int): Int

    @Query("SELECT * FROM currency WHERE position > :position ORDER BY position")
    fun getItemsWithPositionBigger(position: Int): List<CurrencyItem>

    @Query("SELECT * FROM currency WHERE position >= :position ORDER BY position")
    fun getItemsWithPositionBiggerOrEqual(position: Int): List<CurrencyItem>

    @Update(entity = CurrencyItem::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(items: List<CurrencyRate>): Int

    @Transaction
    fun removeFromVisible(code: String) {
        val item = getByCode(code) ?: return
        val nextItems = getItemsWithPositionBigger(item.position)
        updatePosition(code, -1)
        nextItems.forEach { --it.position }
        updateAll(nextItems)
    }

    @Transaction
    fun swapPositions(code: String, anotherCode: String) {
        val item = getByCode(code) ?: return
        val anotherItem = getByCode(anotherCode) ?: return

        if (item.position < 0 || anotherItem.position < 0)
            return

        updatePosition(code, anotherItem.position)
        updatePosition(anotherCode, item.position)
    }

    @Transaction
    fun makeItemsVisible(items: List<CurrencyItem>) {

        val itemsToUpdate = items
                .sortedBy { it.code }
                .sortedBy { it.position }

        val groups = itemsToUpdate.groupBy { it.position >= 0 }

        val alreadyVisibleItems = groups[true]
        if (alreadyVisibleItems?.isNotEmpty() == true) {
            val nextItems = getItemsWithPositionBiggerOrEqual(alreadyVisibleItems.first().position)

            var visPos = 0
            var nextPos = 0
            var newPos = alreadyVisibleItems.first().position

            while (visPos < alreadyVisibleItems.size && nextPos < nextItems.size) {
                if (alreadyVisibleItems[visPos].position <= nextItems[nextPos].position) {
                    ++nextItems[nextPos].position
                    ++visPos
                } else {
                    nextItems[nextPos].position = newPos
                    ++nextPos
                }

                ++newPos
            }


            for (i in visPos until alreadyVisibleItems.size) {
                alreadyVisibleItems[i].position = newPos
                ++newPos
            }

            for (i in nextPos until nextItems.size) {
                nextItems[i].position = newPos
                ++newPos
            }

            val allItems = mutableListOf<CurrencyItem>().apply {
                addAll(alreadyVisibleItems)
                addAll(nextItems)
            }

            updateAll(allItems)
        }


        val nextPosition = countVisibleItems()
        val onlyHiddenItems = groups[false] ?: return
        onlyHiddenItems.forEachIndexed { index, currencyItem ->
            currencyItem.position = nextPosition + index
        }
        updateAll(onlyHiddenItems)
    }

    @Transaction
    fun makeItemsHidden(items: List<CurrencyItem>) {

        val itemsToUpdate = items
                .filter { it.position >= 0 }
                .sortedBy {
                    it.position
                }.map { CurrencyItem(it.code, it.rate, it.position) }

        val firstVisibleItem = itemsToUpdate.firstOrNull()

        if (firstVisibleItem != null) {
            val nextItems = ArrayList(getItemsWithPositionBiggerOrEqual(firstVisibleItem.position))

            var visPos = 0
            var nextPos = 0
            var removed = 0

            while (visPos < itemsToUpdate.size && nextPos < nextItems.size) {
                if (itemsToUpdate[visPos].position == nextItems[nextPos].position) {
                    nextItems[nextPos].position = -1
                    ++removed
                    ++visPos
                    ++nextPos
                } else {

                    nextItems[nextPos].position -= removed

                    if (itemsToUpdate[visPos].position < nextItems[nextPos].position)
                        ++visPos
                    else
                        ++nextPos
                }

            }

            updateAll(nextItems)
        }
    }

    @Update
    fun update(item: CurrencyItem): Int

    @Update
    fun updateAll(items: List<CurrencyItem>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<CurrencyItem>): List<Long>

    @Delete
    fun delete(item: CurrencyItem)
}