package com.apress.gerber.reminders.model.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.apress.gerber.reminders.model.database.entity.Reminder

@Dao
interface ReminderDao : BaseDao<Reminder> {
    companion object {
        const val TABLE_NAME = "reminder"
    }

    @Query("SELECT * FROM $TABLE_NAME")
    fun selectAll(): List<Reminder>?

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