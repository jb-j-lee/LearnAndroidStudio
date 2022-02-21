package com.apress.gerber.reminders.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apress.gerber.reminders.model.database.dao.ReminderDao
import com.apress.gerber.reminders.model.database.entity.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        //TODO Modify Database name
        private const val DATABASE_NAME = "MyReminderDatabase.db"
        private var Instance: ReminderDatabase? = null

        fun getInstance(context: Context): ReminderDatabase? {
            if (Instance == null) {
                synchronized(ReminderDatabase::class) {
                    Instance = Room.databaseBuilder(context, ReminderDatabase::class.java, DATABASE_NAME)
                        //TODO Migration
                        .addMigrations(MigrateUp)
                        .build()
                }
            }
            return Instance
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