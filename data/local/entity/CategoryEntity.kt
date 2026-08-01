package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val accountId: Long = 1,
    val icon: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
