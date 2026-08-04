package com.student.finance.data.repository

import com.student.finance.data.local.dao.SavingGoalDao
import com.student.finance.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingGoalRepository @Inject constructor(private val dao: SavingGoalDao) {
    fun getAll(accountId: Long): Flow<List<SavingGoalEntity>> = dao.getAll(accountId)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(goal: SavingGoalEntity) = dao.insert(goal)
    suspend fun update(goal: SavingGoalEntity) = dao.update(goal)
    suspend fun delete(goal: SavingGoalEntity) = dao.delete(goal)
}
