package com.apress.gerber.reminders.repository

import android.database.Cursor
import androidx.annotation.WorkerThread
import com.apress.gerber.reminders.model.dao.ReminderDao
import com.apress.gerber.reminders.model.entity.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    val reminders = reminderDao.selectAll()

    @WorkerThread
    suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    @WorkerThread
    suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    @WorkerThread
    suspend fun deleteAll() {
        reminderDao.deleteAll()
    }

    @WorkerThread
    suspend fun deleteById(id: Int) {
        reminderDao.deleteById(id)
    }

    fun selectCursor(): Cursor {
        return reminderDao.selectCursor()
    }

    fun selectById(id: Int): Reminder? {
        return reminderDao.selectById(id)
    }
}