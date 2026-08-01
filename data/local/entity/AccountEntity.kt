package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "Cash", "Bank", "E-Wallet"
    val balance: Double = 0.0,
    val icon: String = "account_balance",
    val color: String = "#4CAF50",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
