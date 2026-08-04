package com.student.finance.data.repository

import com.student.finance.data.local.dao.TransactionDao
import com.student.finance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val dao: TransactionDao) {
    fun getAll(accountId: Long): Flow<List<TransactionEntity>> = dao.getAll(accountId)
    fun getByDateRange(accountId: Long, start: Long, end: Long): Flow<List<TransactionEntity>> = dao.getByDateRange(accountId, start, end)
    fun getByCategory(accountId: Long, categoryId: Long): Flow<List<TransactionEntity>> = dao.getByCategory(accountId, categoryId)
    fun getTotalIncome(accountId: Long, start: Long, end: Long): Flow<Double> = dao.getTotalIncome(accountId, start, end)
    fun getTotalExpense(accountId: Long, start: Long, end: Long): Flow<Double> = dao.getTotalExpense(accountId, start, end)
    suspend fun getExpenseForCategoryInRange(accountId: Long, categoryId: Long, start: Long, end: Long) =
        dao.getExpenseForCategoryInRange(accountId, categoryId, start, end)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)
    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
}
