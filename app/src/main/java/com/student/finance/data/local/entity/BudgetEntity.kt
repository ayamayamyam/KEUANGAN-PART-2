package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val month: Int,
    val year: Int,
    val alertThreshold: Double = 0.8,
    val accountId: Long = 1
)
