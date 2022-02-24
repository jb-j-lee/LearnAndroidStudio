package com.apress.gerber.reminders.viewmodel

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.apress.gerber.reminders.model.dao.ReminderDao
import com.apress.gerber.reminders.model.database.ReminderDatabase
import com.apress.gerber.reminders.model.entity.Reminder
import com.apress.gerber.reminders.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository
    private val reminders: LiveData<List<Reminder>>

    init {
        val reminderDao = ReminderDatabase.getInstance(application, CoroutineScope(Dispatchers.IO))?.reminderDao() as ReminderDao
        repository = ReminderRepository(reminderDao)
        reminders = repository.reminders
    }

    fun insert(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(reminder)
        }
    }

    fun update(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(reminder)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    suspend fun selectCursor(): Cursor {
        return viewModelScope.async(Dispatchers.IO) {
            return@async repository.selectCursor()
        }.await()
    }

    suspend fun selectById(id: Int): Reminder? {
        return viewModelScope.async(Dispatchers.IO) {
            return@async repository.selectById(id)
        }.await()
    }
}