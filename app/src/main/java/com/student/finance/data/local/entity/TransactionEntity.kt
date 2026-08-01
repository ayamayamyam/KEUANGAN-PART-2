package com.student.finance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date"), Index("accountId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long?,
    val date: Long,
    val description: String? = null,
    val receiptPhotoPath: String? = null,
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null,
    val accountId: Long = 1
)
