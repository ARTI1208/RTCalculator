package ru.art2000.calculator.model.calculator.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalculatorHistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<HistoryDatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: HistoryDatabaseItem): Long

    @Update
    fun update(item: HistoryDatabaseItem)

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("DELETE FROM history")
    fun clear(): Int
}