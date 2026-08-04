package com.student.finance.data.repository

import com.student.finance.data.local.dao.DebtDao
import com.student.finance.data.local.entity.DebtEntity
import com.student.finance.data.local.entity.DebtStatus
import com.student.finance.data.local.entity.DebtType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(private val dao: DebtDao) {
    fun getAll(accountId: Long): Flow<List<DebtEntity>> = dao.getAll(accountId)
    fun getByType(accountId: Long, type: DebtType): Flow<List<DebtEntity>> = dao.getByType(accountId, type)
    fun getByStatus(accountId: Long, status: DebtStatus): Flow<List<DebtEntity>> = dao.getByStatus(accountId, status)
    fun getTotalUnpaidByType(accountId: Long, type: DebtType): Flow<Double> = dao.getTotalUnpaidByType(accountId, type)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(debt: DebtEntity) = dao.insert(debt)
    suspend fun update(debt: DebtEntity) = dao.update(debt)
    suspend fun delete(debt: DebtEntity) = dao.delete(debt)
}
