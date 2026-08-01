package com.student.finance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.student.finance.data.local.dao.*
import com.student.finance.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        SavingGoalEntity::class,
        ReminderEntity::class,
        DebtEntity::class,
        AccountEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StudentFinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun reminderDao(): ReminderDao
    abstract fun debtDao(): DebtDao
    abstract fun accountDao(): AccountDao

    companion object {
        const val DATABASE_NAME = "student_finance.db"
    }
}
