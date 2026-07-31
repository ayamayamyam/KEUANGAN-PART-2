package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DebtType { LENT, BORROWED }
enum class DebtStatus { PENDING, PAID, OVERDUE }

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val amount: Double,
    val type: DebtType,
    val description: String? = null,
    val date: Long,
    val dueDate: Long? = null,
    val status: DebtStatus = DebtStatus.PENDING,
    val isPaid: Boolean = false,
    val paidDate: Long? = null
)
