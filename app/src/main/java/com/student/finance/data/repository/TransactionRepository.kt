package com.student.finance.data.repository

import com.student.finance.data.local.dao.TransactionDao
import com.student.finance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val dao: TransactionDao) {
    fun getAll(): Flow<List<TransactionEntity>> = dao.getAll()
    fun getByDateRange(start: Long, end: Long): Flow<List<TransactionEntity>> = dao.getByDateRange(start, end)
    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>> = dao.getByCategory(categoryId)
    fun getTotalIncome(start: Long, end: Long): Flow<Double> = dao.getTotalIncome(start, end)
    fun getTotalExpense(start: Long, end: Long): Flow<Double> = dao.getTotalExpense(start, end)
    suspend fun getExpenseForCategoryInRange(categoryId: Long, start: Long, end: Long) =
        dao.getExpenseForCategoryInRange(categoryId, start, end)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)
    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
}
