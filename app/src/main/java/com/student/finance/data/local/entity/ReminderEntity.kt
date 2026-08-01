package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String? = null,
    val triggerTime: Long,
    val isRecurring: Boolean = false,
    val isEnabled: Boolean = true,
    val accountId: Long = 1
)
