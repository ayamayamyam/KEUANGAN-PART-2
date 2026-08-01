package com.student.finance.data.local.dao

import androidx.room.*
import com.student.finance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getAll(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getByDateRange(accountId: Long, start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND categoryId = :categoryId ORDER BY date DESC")
    fun getByCategory(accountId: Long, categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE accountId = :accountId AND type = 'INCOME' AND date BETWEEN :start AND :end")
    fun getTotalIncome(accountId: Long, start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE accountId = :accountId AND type = 'EXPENSE' AND date BETWEEN :start AND :end")
    fun getTotalExpense(accountId: Long, start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE accountId = :accountId AND type = 'EXPENSE' AND categoryId = :categoryId AND date BETWEEN :start AND :end")
    suspend fun getExpenseForCategoryInRange(accountId: Long, categoryId: Long, start: Long, end: Long): Double
}
