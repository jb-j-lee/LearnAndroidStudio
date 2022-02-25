package com.apress.gerber.reminders.model.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.apress.gerber.reminders.model.entity.Reminder

@Dao
interface ReminderDao {
    companion object {
        const val TABLE_NAME = "reminder"
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(reminder: Reminder)

    @Insert
    suspend fun insertAll(reminders: List<Reminder>)

    @Query("SELECT * FROM $TABLE_NAME")
    fun selectAll(): LiveData<List<Reminder>>

    @Query("SELECT * FROM $TABLE_NAME WHERE _id = :id")
    fun selectById(id: Int): Reminder?

    @Query("SELECT * FROM $TABLE_NAME WHERE content = :content")
    fun selectByElement(content: String): Reminder?

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()

    @Query("DELETE FROM $TABLE_NAME WHERE _id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM $TABLE_NAME WHERE content = :content")
    suspend fun deleteByElement(content: String)

    @Transaction
    suspend fun update(entities: List<Reminder>) {
        deleteAll()
        insertAll(entities)
    }

    @Query("SELECT * FROM $TABLE_NAME")
    fun selectCursor(): Cursor
}