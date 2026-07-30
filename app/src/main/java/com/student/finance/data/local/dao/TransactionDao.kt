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

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end")
    fun getTotalIncome(start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    fun getTotalExpense(start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'EXPENSE' AND categoryId = :categoryId AND date BETWEEN :start AND :end")
    suspend fun getExpenseForCategoryInRange(categoryId: Long, start: Long, end: Long): Double
}
