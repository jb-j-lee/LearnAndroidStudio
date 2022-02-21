package com.apress.gerber.reminders.model.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = ABORT)
    suspend fun insert(obj: T)

    @Delete
    suspend fun delete(obj: T)

    @Update(onConflict = REPLACE)
    suspend fun update(obj: T)

    @Insert
    suspend fun insertAll(obj: List<T>)
}