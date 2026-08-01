package com.student.finance.data.repository

import com.student.finance.data.local.dao.BudgetDao
import com.student.finance.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(private val dao: BudgetDao) {
    fun getForMonth(accountId: Long, month: Int, year: Int): Flow<List<BudgetEntity>> = dao.getForMonth(accountId, month, year)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(budget: BudgetEntity) = dao.insert(budget)
    suspend fun update(budget: BudgetEntity) = dao.update(budget)
    suspend fun delete(budget: BudgetEntity) = dao.delete(budget)
}
