package com.apress.gerber.reminders.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    var content: String,
    var important: Int
)