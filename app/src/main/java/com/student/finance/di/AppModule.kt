package com.student.finance.di

import android.content.Context
import androidx.room.Room
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.StudentFinanceDatabase
import com.student.finance.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StudentFinanceDatabase =
        Room.databaseBuilder(
            context,
            StudentFinanceDatabase::class.java,
            StudentFinanceDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideTransactionDao(db: StudentFinanceDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: StudentFinanceDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideBudgetDao(db: StudentFinanceDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideSavingGoalDao(db: StudentFinanceDatabase): SavingGoalDao = db.savingGoalDao()

    @Provides
    fun provideReminderDao(db: StudentFinanceDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideDebtDao(db: StudentFinanceDatabase): DebtDao = db.debtDao()

    @Provides
    fun provideAccountDao(db: StudentFinanceDatabase): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)
}
