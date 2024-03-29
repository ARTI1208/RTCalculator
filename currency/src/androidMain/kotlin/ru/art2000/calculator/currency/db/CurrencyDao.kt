package ru.art2000.calculator.currency.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.currency.db.model.CurrencyDbRate
import ru.art2000.calculator.currency.db.model.CurrencyDbItem

@Dao
internal interface CurrencyDao {

    @Query("SELECT * FROM currency WHERE position >= 0 ORDER BY position")
    fun getVisibleItems(): Flow<List<CurrencyDbItem>>

    @Query("SELECT COUNT(*) FROM currency WHERE position >= 0")
    suspend fun countVisibleItems(): Int

    @Query("SELECT * FROM currency WHERE position < 0 ORDER BY codeLetter")
    fun getHiddenItems(): Flow<List<CurrencyDbItem>>

    @Query("SELECT * FROM currency WHERE codeLetter = :code ")
    suspend fun getByCode(code: String): CurrencyDbItem?

    @Query("UPDATE currency SET position = :position WHERE codeLetter = :code")
    suspend fun updatePosition(code: String, position: Int): Int

    @Query("SELECT * FROM currency WHERE position > :position ORDER BY position")
    suspend fun getItemsWithPositionBigger(position: Int): List<CurrencyDbItem>

    @Query("SELECT * FROM currency WHERE position >= :position ORDER BY position")
    suspend fun getItemsWithPositionBiggerOrEqual(position: Int): List<CurrencyDbItem>

    @Update(entity = CurrencyDbItem::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRates(items: List<CurrencyDbRate>): Int

    @Update
    suspend fun updateAll(items: List<CurrencyDbItem>): Int

    @Transaction
    suspend fun removeFromVisible(code: String) {
        val item = getByCode(code) ?: return
        val nextItems = getItemsWithPositionBigger(item.position)
        updatePosition(code, -1)
        nextItems.forEach { --it.position }
        updateAll(nextItems)
    }

    @Transaction
    suspend fun makeItemsVisible(items: List<CurrencyDbItem>) {

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

            val allItems = mutableListOf<CurrencyDbItem>().apply {
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
    suspend fun makeItemsHidden(items: List<CurrencyDbItem>) {

        val itemsToUpdate = items
                .filter { it.position >= 0 }
                .sortedBy {
                    it.position
                }.map { CurrencyDbItem(it.code, it.rate, it.position) }

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

}