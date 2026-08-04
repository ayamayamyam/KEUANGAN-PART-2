package com.student.finance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.student.finance.data.local.dao.*

@Database(
    entities = [
        com.student.finance.data.local.entity.TransactionEntity::class,
        com.student.finance.data.local.entity.CategoryEntity::class,
        com.student.finance.data.local.entity.BudgetEntity::class,
        com.student.finance.data.local.entity.SavingGoalEntity::class,
        com.student.finance.data.local.entity.ReminderEntity::class,
        com.student.finance.data.local.entity.DebtEntity::class,
        com.student.finance.data.local.entity.AccountEntity::class
    ],
    version = 3,
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
