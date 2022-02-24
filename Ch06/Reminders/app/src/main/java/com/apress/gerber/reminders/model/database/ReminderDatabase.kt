package com.apress.gerber.reminders.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apress.gerber.reminders.BuildConfig
import com.apress.gerber.reminders.model.dao.ReminderDao
import com.apress.gerber.reminders.model.entity.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    private class ReminderCallback(private val coroutineScope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //TODO makeDummy
            if (!BuildConfig.DEBUG) {
                return
            }

            INSTANCE?.let {
                coroutineScope.launch {
                    makeDummy(it.reminderDao())
                }
            }
        }

        suspend fun makeDummy(reminderDao: ReminderDao) {
            reminderDao.deleteAll()

            for (i in 1..20) {
                reminderDao.insert(Reminder(i, "TEST $i", i % 2))
            }
            reminderDao.insert(Reminder(100, "ReminderCallback 100", 1))
            reminderDao.insert(Reminder(101, "ReminderCallback 101", 0))
        }
    }

    companion object {
        private const val DATABASE_NAME = "MyReminderDatabase.db"
        private var INSTANCE: ReminderDatabase? = null

        fun getInstance(context: Context, coroutineScope: CoroutineScope): ReminderDatabase? {
            if (INSTANCE == null) {
                synchronized(ReminderDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, ReminderDatabase::class.java, DATABASE_NAME)
                        .addCallback(ReminderCallback(coroutineScope))
                        .addMigrations(MigrateUp)
                        .build()
                }
            }
            return INSTANCE
        }
    }

    private object MigrateUp : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS 'table2'('_id' INTEGER PRIMARY KEY NOT NULL, 'element' TEXT NOT NULL)")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private object MigrateDown : Migration(2, 1) {
        override fun migrate(database: SupportSQLiteDatabase) {
            try {
                database.query("DROP TABLE table2")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}