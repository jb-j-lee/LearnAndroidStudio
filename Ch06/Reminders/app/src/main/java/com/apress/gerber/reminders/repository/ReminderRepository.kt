package com.apress.gerber.reminders.repository

import android.database.Cursor
import androidx.annotation.WorkerThread
import com.apress.gerber.reminders.model.dao.ReminderDao
import com.apress.gerber.reminders.model.entity.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    val reminders = reminderDao.selectAll()

    //TODO unit test 를 위한
    @WorkerThread
    suspend fun insert(reminder: Reminder) = withContext(ioDispatcher) {
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